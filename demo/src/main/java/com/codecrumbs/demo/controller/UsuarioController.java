package com.codecrumbs.demo.controller;

import java.math.BigDecimal;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.catalina.connector.Response;
import org.hibernate.JDBCException;
import org.hibernate.exception.GenericJDBCException;
import org.modelmapper.internal.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;

import com.codecrumbs.demo.dto.DashboardProgressoDTO;
import com.codecrumbs.demo.dto.UsuarioBasicoDTO;
import com.codecrumbs.demo.dto.UsuarioCreateDTO;
import com.codecrumbs.demo.dto.UsuarioLoginDTO;
import com.codecrumbs.demo.dto.mapper.UsuarioMapper;
import com.codecrumbs.demo.exceptions.ApelidoAlreadyUsedException;
import com.codecrumbs.demo.exceptions.EmailAlreadyUsedException;
import com.codecrumbs.demo.exceptions.InvalidCredentialsException;
import com.codecrumbs.demo.model.UsuarioModel;
import com.codecrumbs.demo.repository.UsuarioRepository;
import com.codecrumbs.enumeracao.LinguagemEnum;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("usuarios")
public class UsuarioController {
    
    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private UsuarioMapper mapper;

    /* JDBC vai ser usado no DTO do dashboard, já que ele não é um model do banco, o JPA não consegue lidar com ele*/ 
    @Autowired
    private JdbcTemplate jdbc;

    @GetMapping
    public ResponseEntity<List<UsuarioBasicoDTO>> getUsuarios(){
        List<UsuarioModel> lista_usuarios = repository.findAll();
        List<UsuarioBasicoDTO> lista_dtos = new ArrayList<>();

        lista_usuarios.forEach(user -> {
            UsuarioBasicoDTO dto = mapper.toUsuarioBasicoDTO(user);
            lista_dtos.add(dto);
        });
        
        return ResponseEntity.status(200).body(lista_dtos);
    }

    @PostMapping
    public ResponseEntity<String> newUsuario(@RequestBody @Valid UsuarioCreateDTO dto) throws SQLException{
        try{
            Optional<UsuarioModel> novoUsuario = repository.cadastrarUsuario(dto.getEmail(), dto.getSenha(), dto.getApelido());
            return ResponseEntity.status(201).body("Cadastrado com Sucesso");
        } 
        catch(Throwable e){
            String msg_error = e.getMessage();
            
            if(msg_error.contains("45001")){
                throw new EmailAlreadyUsedException("Email já está em uso");
            }

            if(msg_error.contains("45002")){
                throw new ApelidoAlreadyUsedException("Apelido já está em uso");
            }

            throw new IllegalStateException("Erro: "+msg_error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioModel> login(@RequestBody @Valid UsuarioLoginDTO dto){
        try{
            Optional<UsuarioModel> usuario = repository.fazerLogin(dto.getEmail(), dto.getSenha());
            return ResponseEntity.status(200).body(usuario.get());
        } catch(Throwable e){
            String msg_error = e.getMessage();

            if(msg_error.contains("45003")){
                throw new InvalidCredentialsException("Email ou senha invalidos");
            }

            throw new IllegalStateException("Erro: "+msg_error);
        }
    }

    @GetMapping("{id}/dashboard")
    public ResponseEntity<DashboardProgressoDTO> getDashboardInfo(@PathVariable("id") Long id){
        /* 
         * Não foi possível usar o JPARepository nesse método, pois o DTO do dashboard não possui uma tabela no banco
         * Assim foi necessário fazer a injeção do JdbcTemplate para realizar requisições manuais ao banco
         * Abaixo, é feita uma call simplificada ao procedure que calcula os dados do DTO passando o Id do usuário
         */
        SimpleJdbcCall call = new SimpleJdbcCall(jdbc)
            .withSchemaName("codecrumbs")
            .withProcedureName("proc_carregar_dashboard");

        /* Executando a Query e passando um parametro (id do usuário) para a procedure */
        SqlParameterSource in = new MapSqlParameterSource().addValue("param_id", id);
        
        /* Com o resultado da query (in) mapeamos os parametros de saida OUT da procedure para um json
         * Esse json é chamado de (out) e cada chave dele traz um valor OUT da procedure
         */
        Map<String, Object> out = call.execute(in);


        /* Criamos um objeto DTO e setamos manualmente os dados dele com os do json (out) usando o out.get(chave)
         * Essa chave é o nome do parametro de saida OUT lá na procedure.
         */
        DashboardProgressoDTO dto = new DashboardProgressoDTO();
        dto.setPercQuizzesConcluidos((BigDecimal) out.get("porcent_quizzes"));
        dto.setPercExerciciosConcluidos((BigDecimal) out.get("porcent_exercicios"));
        dto.setTotalFlashCards((Integer) out.get("quant_flashcards"));
        dto.setLinguagemFavorita(
            LinguagemEnum.valueOf((String) out.get("linguagem_favorita"))
        );

        return ResponseEntity.status(200).body(dto);
    }
}
