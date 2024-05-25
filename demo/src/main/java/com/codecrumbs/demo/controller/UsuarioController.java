package com.codecrumbs.demo.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codecrumbs.demo.dto.DashboardProgressoDTO;
import com.codecrumbs.demo.dto.UsuarioBasicoDTO;
import com.codecrumbs.demo.dto.UsuarioCreateDTO;
import com.codecrumbs.demo.dto.UsuarioLoginDTO;
import com.codecrumbs.demo.model.UsuarioModel;
import com.codecrumbs.demo.service.UsuarioService;

import jakarta.validation.Valid;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("usuarios")
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioBasicoDTO>> getUsuarios(){
        List<UsuarioBasicoDTO> lista_dtos = usuarioService.getUsuarios();

        return ResponseEntity.status(200).body(lista_dtos);
    }

    @PostMapping
    public ResponseEntity<String> criarNovoUsuario(@RequestBody @Valid UsuarioCreateDTO dto) throws SQLException{
        UsuarioModel novoUsuario = usuarioService.criarNovoUsuario(dto);
        
        if(novoUsuario != null){
            return ResponseEntity.status(201).body("Cadastrado com Sucesso");
        }

        return ResponseEntity.status(201).body("Erro ao realizar cadastro");
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioModel> fazerLogin(@RequestBody @Valid UsuarioLoginDTO dto){
        UsuarioModel dados_usuario = usuarioService.fazerLogin(dto);

        return ResponseEntity.status(200).body(dados_usuario);
    }

    @GetMapping("{id}/dashboard")
    public ResponseEntity<DashboardProgressoDTO> getDashboardInfo(@PathVariable("id") Integer id){
        DashboardProgressoDTO dto = usuarioService.getDashboardProgressoDTO(id);

        return ResponseEntity.status(200).body(dto);
    }
}
