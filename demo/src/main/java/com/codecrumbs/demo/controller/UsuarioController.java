package com.codecrumbs.demo.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codecrumbs.demo.dto.UsuarioBasicoDTO;
import com.codecrumbs.demo.dto.UsuarioCreateDTO;
import com.codecrumbs.demo.dto.UsuarioLoginDTO;
import com.codecrumbs.demo.dto.mapper.UsuarioMapper;
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
    public ResponseEntity<?> newUsuario(@RequestBody @Valid UsuarioCreateDTO dto){
        try{
            Optional<UsuarioModel> novoUsuario = repository.cadastrarUsuario(dto.getEmail(), dto.getSenha(), dto.getApelido());
            return ResponseEntity.status(201).body(novoUsuario.get());
        } 
        catch(Throwable e){
            String msg_error = e.getMessage();
            
            if(msg_error.contains(dto.getApelido())){
                return ResponseEntity.status(409).body("Apelido já está em uso");
            }

            if(msg_error.contains(dto.getEmail())){
                return ResponseEntity.status(409).body("Email já está em uso");
            }

            return ResponseEntity.status(409).body("Erro interno ao realizar cadastro");
        }
    }

    @PostMapping("login")
    public ResponseEntity<UsuarioModel> login(@RequestBody @Valid UsuarioLoginDTO dto){
        Optional<UsuarioModel> usuario = repository.fazerLogin(dto.getEmail(), dto.getSenha());

        if(usuario.isPresent()){
            return ResponseEntity.status(200).body(usuario.get());
        }

        return ResponseEntity.status(404).body(null);
    }
}
