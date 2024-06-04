package com.codecrumbs.demo.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

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

@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private UsuarioMapper mapper;

    /* JDBC vai ser usado no DTO do dashboard, já que ele não é um model do banco, o JPA não consegue lidar com ele*/ 
    @Autowired
    private JdbcTemplate jdbc;

    public List<UsuarioBasicoDTO> getUsuarios(){
        List<UsuarioModel> lista_usuarios = repository.findAll();
        List<UsuarioBasicoDTO> lista_dtos = new ArrayList<>();

        lista_usuarios.forEach(user -> {
            UsuarioBasicoDTO dto = mapper.toUsuarioBasicoDTO(user);
            lista_dtos.add(dto);
        });

        return lista_dtos;
    }

    public UsuarioBasicoDTO criarNovoUsuario(UsuarioCreateDTO dto){
        /**
         * A lógica de Cadastro está no banco, caso haja erro ao logar, o proprio MySQL vai mandar um erro, que está sendo pego aqui no Catch
         * Veja o arquivo "QUERY_PROCEDURES" na pasta resources/database 
         */
        try{
            Optional<UsuarioModel> novoUsuario = repository.cadastrarUsuario(dto.getEmail(), dto.getSenha(), dto.getApelido());
            
            
            if(novoUsuario.isPresent()){
                UsuarioBasicoDTO dtoBasico = mapper.toUsuarioBasicoDTO(novoUsuario.get());
                return dtoBasico;
            }
            
            return null;
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

    public UsuarioModel fazerLogin(UsuarioLoginDTO dto){
        /**
         * A lógica de Login está no banco, caso haja erro ao logar, o proprio MySQL vai mandar um erro, que está sendo pego aqui no Catch
         * Veja o arquivo "QUERY_PROCEDURES" na pasta resources/database 
         */
        try{
            Optional<UsuarioModel> dados_usuario = repository.fazerLogin(dto.getEmail(), dto.getSenha());
            
            if(dados_usuario.isPresent()){
                return dados_usuario.get();
            }

            return null;
            
            
        } catch(Throwable e){
            String msg_error = e.getMessage();

            if(msg_error.contains("45003")){
                throw new InvalidCredentialsException("Email ou senha invalidos");
            }

            throw new IllegalStateException("Erro: "+msg_error);
        }
    }

    public DashboardProgressoDTO getDashboardProgressoDTO(Integer id){
        /* 
         * Não foi possível usar o JPARepository nesse método, pois o DTO do dashboard não possui uma tabela no banco
         * Assim foi necessário fazer a injeção do JdbcTemplate para realizar requisições manuais ao banco
         * Abaixo, é feita uma call simplificada ao procedure que calcula os dados do DTO passando o Id do usuário
         * Veja o arquivo "QUERY_PROCEDURES" na pasta resources/databases e procure o procedure "proc_carregar_dashboard" para entender melhor
         */
        SimpleJdbcCall call = new SimpleJdbcCall(jdbc)
            .withSchemaName("codecrumbs")
            .withProcedureName("proc_carregar_dashboard");

        /* Executando a Query e passando um parametro (id do usuário) para a procedure. O nome é o mesmo que está la na procedure */
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

        return dto;
    }
}
