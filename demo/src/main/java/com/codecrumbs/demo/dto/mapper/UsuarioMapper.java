package com.codecrumbs.demo.dto.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.codecrumbs.demo.dto.UsuarioCreateDTO;
import com.codecrumbs.demo.model.UsuarioModel;

@Component
public class UsuarioMapper {
    
    @Autowired
    private ModelMapper mapper;

    public UsuarioModel toModel(UsuarioCreateDTO dto){
        UsuarioModel usuario = mapper.map(dto, UsuarioModel.class);
        return usuario;
    }
}
