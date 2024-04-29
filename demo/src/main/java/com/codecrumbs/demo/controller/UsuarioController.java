package com.codecrumbs.demo.controller;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.JDBCException;
import org.hibernate.exception.GenericJDBCException;
import org.modelmapper.internal.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
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

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("usuarios")
public class UsuarioController {
    
    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private UsuarioMapper mapper;

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
}
