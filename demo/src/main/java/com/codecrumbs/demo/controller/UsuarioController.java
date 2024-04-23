package com.codecrumbs.demo.controller;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Optional;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codecrumbs.demo.dto.UsuarioCreateDTO;
import com.codecrumbs.demo.dto.UsuarioLoginDTO;
import com.codecrumbs.demo.model.UsuarioModel;
import com.codecrumbs.demo.repository.UsuarioRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("usuarios")
public class UsuarioController {
    
    @Autowired
    private UsuarioRepository repository;

    @GetMapping
    public ResponseEntity<List<UsuarioModel>> getUsuarios(){
        List<UsuarioModel> lista_usuarios = repository.findAll();
        
        return ResponseEntity.status(200).body(lista_usuarios);
    }

    @PostMapping
    public ResponseEntity newUsuario(@RequestBody @Valid UsuarioCreateDTO dto){
        try{
            Optional<UsuarioModel> novoUsuario = repository.cadastrarUsuario(dto.getEmail(), dto.getSenha(), dto.getApelido());
            return ResponseEntity.status(200).body(novoUsuario.get());
        } 
        catch(Throwable e){
            String msg_error = e.getMessage();
            
            if(msg_error.contains("t01_usuario.A01_Apelido")){
                return ResponseEntity.status(409).body("Apelido já está em uso");
            }

            if(msg_error.contains("t02_credenciais.A02_email")){
                return ResponseEntity.status(409).body("Email já está em uso");
            }

            return ResponseEntity.status(409).body("Erro interno ao realizar cadastro");
        }


    }
}
