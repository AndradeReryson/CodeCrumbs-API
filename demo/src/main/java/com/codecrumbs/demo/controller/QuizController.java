package com.codecrumbs.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codecrumbs.demo.dto.QuizComPerguntasDTO;
import com.codecrumbs.demo.dto.QuizComProgressoDTO;
import com.codecrumbs.demo.dto.mapper.QuizMapper;
import com.codecrumbs.demo.dto.mapper.UsuarioMapper;
import com.codecrumbs.demo.model.QuizModel;
import com.codecrumbs.demo.repository.QuizRepository;

@RestController
@CrossOrigin("*")
@RequestMapping("quizzes")
public class QuizController {
    
    @Autowired
    private QuizRepository repository;

    @Autowired
    private QuizMapper mapper;

    /** 
     * JDBC vai ser usado no DTO de Quiz com Progresso, para mapear o procedure direto pro DTO
     *  */
    @Autowired
    private JdbcTemplate jdbc;

    @GetMapping 
    public ResponseEntity<List<QuizModel>> getAllQuizzes(){
        Pageable pageable = PageRequest.of(0, 2);
        List<QuizModel> lista = repository.findAll(pageable).getContent();

        return ResponseEntity.status(200).body(lista);
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
}
