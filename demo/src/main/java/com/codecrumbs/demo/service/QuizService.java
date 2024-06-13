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
import com.codecrumbs.demo.dto.ProgressoQuizCreateDTO;
import com.codecrumbs.demo.dto.mapper.QuizMapper;
import com.codecrumbs.demo.exceptions.InvalidQuizException;
import com.codecrumbs.demo.exceptions.InvalidUsuarioException;
import com.codecrumbs.demo.model.ProgressoQuizModel;
import com.codecrumbs.demo.model.QuizModel;
import com.codecrumbs.demo.model.QuizPerguntaModel;
import com.codecrumbs.demo.model.QuizRespostaModel;
import com.codecrumbs.demo.model.UsuarioModel;
import com.codecrumbs.demo.repository.ProgressoQuizRepository;
import com.codecrumbs.demo.repository.QuizPerguntaRepository;
import com.codecrumbs.demo.repository.QuizRepository;
import com.codecrumbs.demo.repository.QuizRespostaRepository;
import com.codecrumbs.demo.repository.UsuarioRepository;

@Service
public class QuizService {
    

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizPerguntaRepository perguntaRepository;

    @Autowired
    private QuizRespostaRepository respostaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProgressoQuizRepository progressoRepository;

    @Autowired
    private QuizMapper mapper;

    /** 
     * JDBC vai ser usado no DTO de Quiz com Progresso, para mapear o procedure direto pro DTO
     *  */
    @Autowired
    private JdbcTemplate jdbc;

    public List<QuizComPerguntasDTO> getAllQuizzes(){
        Pageable pageable = PageRequest.of(0, 16);
        List<QuizModel> lista_model = quizRepository.findAll(pageable).getContent();
        List<QuizComPerguntasDTO> lista_dto = new ArrayList<>();

        lista_model.forEach(model -> {
            lista_dto.add(mapper.toQuizComPerguntasDTO(model));
        });

        return lista_dto;
    }

    public List<QuizComProgressoDTO> getAllQuizzesComProgressoDoUsuario(Integer id_usuario, String linguagem, Integer page){
        Integer quizzes_por_pagina = 16;
        Integer codigo_linguagem;

        switch (linguagem) {
            case "CSS":
                codigo_linguagem = 1; 
                break;
            case "SQL":
                codigo_linguagem = 2;
                break;
            default:
                codigo_linguagem = 1;
                break;
        }

        SimpleJdbcCall call = new SimpleJdbcCall(jdbc)
            .withSchemaName("codecrumbs")
            .withProcedureName("proc_buscar_quizzes_ref_usuario")
            .returningResultSet("resultados", BeanPropertyRowMapper.newInstance(QuizComProgressoDTO.class));
        
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("param_id", id_usuario);
        parametros.put("param_linguagem", codigo_linguagem);                // É o valor do enum das linguagens (CSS == 1, SQL == 2, ... etc)
        parametros.put("param_limit", quizzes_por_pagina);                  // LIMIT é a quantidade de resultados por pagina
        parametros.put("param_offset", (page - 1) * quizzes_por_pagina);    // OFFSET é quantos resultados ele deve pular, ou seja, a pagina controla o OFFSET. pagina 2 = pule os 16 primeiros;
        
        SqlParameterSource in = new MapSqlParameterSource().addValues(parametros);

        Map<String, Object> out = call.execute(in);

        @SuppressWarnings("unchecked")
        List<QuizComProgressoDTO> lista = (List<QuizComProgressoDTO>) out.get("resultados"); 
        
        return lista;
        
    }

    public List<QuizComProgressoDTO> getMeusQuizzesComProgresso(Integer id_usuario, String linguagem, Integer page){
        Integer quizzes_por_pagina = 16;
        Integer codigo_linguagem;

        switch (linguagem) {
            case "CSS":
                codigo_linguagem = 1; 
                break;
            case "SQL":
                codigo_linguagem = 2;
                break;
            default:
                codigo_linguagem = 1;
                break;
        }

        SimpleJdbcCall call = new SimpleJdbcCall(jdbc)
            .withSchemaName("codecrumbs")
            .withProcedureName("proc_buscar_meus_quizzes")
            .returningResultSet("resultados", BeanPropertyRowMapper.newInstance(QuizComProgressoDTO.class));
        
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("param_id", id_usuario);
        parametros.put("param_linguagem", codigo_linguagem);                // É o valor do enum das linguagens (CSS == 1, SQL == 2, ... etc)
        parametros.put("param_limit", quizzes_por_pagina);                  // LIMIT é a quantidade de resultados por pagina
        parametros.put("param_offset", (page - 1) * quizzes_por_pagina);    // OFFSET é quantos resultados ele deve pular, ou seja, a pagina controla o OFFSET. pagina 2 = pule os 16 primeiros;
        
        SqlParameterSource in = new MapSqlParameterSource().addValues(parametros);

        Map<String, Object> out = call.execute(in);

        @SuppressWarnings("unchecked")
        List<QuizComProgressoDTO> lista = (List<QuizComProgressoDTO>) out.get("resultados"); 
    
        return lista;

    }

    public Optional<QuizComPerguntasDTO> getQuizComPerguntas(Integer id_quiz){
        Optional<QuizModel> model = quizRepository.findById(id_quiz);
        QuizComPerguntasDTO dto = null;

        if(model.isPresent()){
            dto = mapper.toQuizComPerguntasDTO(model.get());
        }

        Optional<QuizComPerguntasDTO> opt_dto = Optional.of(dto);

        return opt_dto;
    }

    public QuizModel criarNovoQuiz(QuizCreateDTO dto){
        Optional<UsuarioModel> criador = usuarioRepository.findById(dto.getId_criador());
    
        if(criador.isEmpty()){
            throw new InvalidUsuarioException("Usuario informado nao existe");
        }

        // guardar o modelQuiz que a operação de Save() devolve, vamos precisar dele;
        QuizModel newQuiz = quizRepository.save(new QuizModel(dto.getTitulo(),
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
        Optional<QuizModel> resultQuiz = quizRepository.findById(newQuiz.getId());

        if(resultQuiz.isEmpty()){
           throw new IllegalStateException("Erro ao criar novo quiz");
        }
        
        return resultQuiz.get();
    }
 
    public Optional<ProgressoQuizModel> salvarProgressoQuiz(ProgressoQuizCreateDTO dto){
        /** Pra fazer o SAVE com o JPA vamos precisar de:
         *      - Model do Usuario;
         *      - Model do Quiz
         *      - Nota tirada
          */

        Optional<ProgressoQuizModel> optProgressoExistente = progressoRepository.findByQuizAndUserId(dto.getId_quiz(), dto.getId_usuario());

        if(optProgressoExistente.isPresent()){
            ProgressoQuizModel modelProgressoExistente = optProgressoExistente.get();
        
            if(modelProgressoExistente.getNota().compareTo(dto.getNota()) == -1){
                modelProgressoExistente.setNota(dto.getNota());
                progressoRepository.save(modelProgressoExistente);
            }
            

            return Optional.of(modelProgressoExistente);
        } else {
            Optional<UsuarioModel> optUsuario = usuarioRepository.findById(dto.getId_usuario());

            if(optUsuario.isEmpty()) {
                throw new InvalidUsuarioException("Id de usuário não existe");
            }

            Optional<QuizModel> optQuiz = quizRepository.findById(dto.getId_quiz());

                if(optQuiz.isEmpty()){
                    throw new InvalidQuizException("Id do quiz não existe");
                }

            // Juntando os dados necessários
            UsuarioModel usuario = optUsuario.get();
            QuizModel quiz = optQuiz.get();

            ProgressoQuizModel modelProgresso = new ProgressoQuizModel(dto.getNota(), usuario, quiz);
            
            // usando o repository do progresso
            ProgressoQuizModel retornoProgressoCriado = progressoRepository.save(modelProgresso);
            Optional<ProgressoQuizModel> optProgresso = Optional.of(retornoProgressoCriado);

            return optProgresso;
            }
        
    }

    public QuizModel alterarQuiz(Integer id_quiz, QuizCreateDTO dto){
        // TODO DO
        return null;
    }
}