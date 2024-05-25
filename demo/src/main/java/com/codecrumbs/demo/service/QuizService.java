package com.codecrumbs.demo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

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

@Service
public class QuizService {
    
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


    public List<QuizComPerguntasDTO> getAllQuizzes(){
        Pageable pageable = PageRequest.of(0, 16);
        List<QuizModel> lista_model = repository.findAll(pageable).getContent();
        List<QuizComPerguntasDTO> lista_dto = new ArrayList<>();

        lista_model.forEach(model -> {
            lista_dto.add(mapper.toQuizComPerguntasDTO(model));
        });

        return lista_dto;
    }

    public List<QuizComProgressoDTO> getAllQuizzesComProgressoDoUsuario(Integer id_usuario, Integer page){
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

        @SuppressWarnings("unchecked")
        List<QuizComProgressoDTO> lista = (List<QuizComProgressoDTO>) out.get("resultados"); 
        
        return lista;
        
    }

    public QuizComPerguntasDTO getQuizComPerguntas(Integer id_quiz){
        Optional<QuizModel> model = repository.findById(id_quiz);

        if(model.isPresent()){
            QuizComPerguntasDTO dto = mapper.toQuizComPerguntasDTO(model.get());
            return dto;
        }

        return null;
    }

    public QuizModel criarNovoQuiz(QuizCreateDTO dto){
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
        
        return resultQuiz.get();
    }
}