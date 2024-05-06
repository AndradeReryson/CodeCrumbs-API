package com.codecrumbs.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codecrumbs.demo.dto.QuizComPerguntasDTO;
import com.codecrumbs.demo.dto.QuizComProgressoDTO;
import com.codecrumbs.demo.dto.QuizCreateDTO;
import com.codecrumbs.demo.dto.mapper.QuizMapper;
import com.codecrumbs.demo.exceptions.InvalidUsuarioException;
import com.codecrumbs.demo.model.QuizModel;
import com.codecrumbs.demo.model.QuizPerguntaModel;
import com.codecrumbs.demo.model.QuizRespostaModel;
import com.codecrumbs.demo.model.UsuarioModel;
import com.codecrumbs.demo.repository.QuizPerguntaRepository;
import com.codecrumbs.demo.repository.QuizRepository;
import com.codecrumbs.demo.repository.QuizRespostaRepository;
import com.codecrumbs.demo.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@RestController
@CrossOrigin("*")
@RequestMapping("quizzes")
public class QuizController {
    
    @Autowired
    private QuizRepository repository;

    @Autowired
    private QuizPerguntaRepository perguntaRepository;

    @Autowired
    private QuizRespostaRepository respostaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private QuizMapper mapper;

    /** 
     * JDBC vai ser usado no DTO de Quiz com Progresso, para mapear o procedure direto pro DTO
     *  */
    @Autowired
    private JdbcTemplate jdbc;

    @GetMapping 
    public ResponseEntity<List<QuizComPerguntasDTO>> getAllQuizzes(){
        Pageable pageable = PageRequest.of(0, 16);
        List<QuizModel> lista_model = repository.findAll(pageable).getContent();
        List<QuizComPerguntasDTO> lista_dto = new ArrayList<>();

        lista_model.forEach(model -> {
            lista_dto.add(mapper.toQuizComPerguntasDTO(model));
        });

        return ResponseEntity.status(200).body(lista_dto);
    }

    /**
     * Essa rota traz uma lista de QuizComProgressoDTO, isto é:
     * - Cada DTO tem os atributos do quiz, um atributo que diz se o usuário concluiu o quiz e a nota dele
     * 
     * Na rota, deve ser passado o id do usuario como referencia, e a pagina que vai ser carregada
     * Está sendo feito uma paginação, onde cada pagina vai ter 16 quizzes
     * 
     */
    @GetMapping("ref/{id_usuario}/pagina/{page}")
    public ResponseEntity<List<QuizComProgressoDTO>> getAllQuizzesComProgressoDoUsuario(@PathVariable("id_usuario") Integer id_usuario,
                                                                                @PathVariable("page") Integer page){
        Integer quizzes_por_pagina = 16;

        SimpleJdbcCall call = new SimpleJdbcCall(jdbc)
            .withSchemaName("codecrumbs")
            .withProcedureName("proc_buscar_quizzes_ref_usuario")
            .returningResultSet("resultados", BeanPropertyRowMapper.newInstance(QuizComProgressoDTO.class));
        
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("param_id", id_usuario);
        parametros.put("param_limit", quizzes_por_pagina);                  // LIMIT é a quantidade de resultados por pagina
        parametros.put("param_offset", (page - 1) * quizzes_por_pagina);    // OFFSET é quantos resultados ele deve pular, ou seja, a pagina controla o OFFSET. pagina 2 = pule os 16 primeiros;
        
        SqlParameterSource in = new MapSqlParameterSource().addValues(parametros);

        Map<String, Object> out = call.execute(in);

        List<QuizComProgressoDTO> lista = (List<QuizComProgressoDTO>) out.get("resultados");

        return ResponseEntity.status(200).body(lista);
    }

    @GetMapping("{id_quiz}") 
    public ResponseEntity<QuizComPerguntasDTO> getQuizComPerguntas(@PathVariable("id_quiz") Integer id_quiz){
        Optional<QuizModel> model = repository.findById(id_quiz);

        if(model.isPresent()){
            QuizComPerguntasDTO dto = mapper.toQuizComPerguntasDTO(model.get());
            return ResponseEntity.status(200).body(dto);
        }

        return ResponseEntity.status(404).body(null);
    }

    @Transactional
    @PostMapping
    public ResponseEntity<QuizModel> criarNovoQuiz(@RequestBody QuizCreateDTO dto){
        Optional<UsuarioModel> criador = usuarioRepository.findById(dto.getId_criador());
    
        if(criador.isEmpty()){
            throw new InvalidUsuarioException("Usuario informado nao existe");
        }

        // guardar o modelQuiz que a operação de Save() devolve, vamos precisar dele;
        QuizModel newQuiz = repository.save(new QuizModel(dto.getTitulo(),
                                                            dto.getLinguagem(),
                                                            criador.get()));

        // para cada pergunta do quiz, vamos criar um objeto novo e salvá-las
        dto.getLista_perguntas().forEach(pergunta -> {
            QuizPerguntaModel newPergunta = perguntaRepository.save(new QuizPerguntaModel(pergunta.getEnunciado(), newQuiz));

            // agora, vamos percorrer as respostas dessa pergunta e salva-las no banco também         
            pergunta.getLista_respostas().forEach(resposta -> {
                respostaRepository.save(new QuizRespostaModel(resposta.getTexto(),
                                                                resposta.getIsCorreta(),
                                                                newPergunta));
            });
        });
        
        // agora vamos puxar novamente o novo quiz, que dessa vez vai vir com as chaves estrangeiras do banco corretamente
        Optional<QuizModel> resultQuiz = repository.findById(newQuiz.getId());

        if(resultQuiz.isEmpty()){
           throw new IllegalStateException("Erro ao criar novo quiz");
        }
        
        return ResponseEntity.status(201).body(resultQuiz.get());
    }


}
