package com.codecrumbs.demo.dto.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.codecrumbs.demo.dto.UsuarioBasicoDTO;
import com.codecrumbs.demo.model.CredenciaisModel;
import com.codecrumbs.demo.model.UsuarioModel;

@Component
public class UsuarioMapper {
    
    @Autowired
    private ModelMapper mapper;

    public UsuarioBasicoDTO toUsuarioBasicoDTO(UsuarioModel usuario){
        UsuarioBasicoDTO dto;
        dto = mapper.map(usuario, UsuarioBasicoDTO.class);
        dto = mapper.map(usuario.getCredenciais(), UsuarioBasicoDTO.class);
        /*
         * Erro: o apelido não está sendo mapeado, testar amanhã
         */
        return dto;
    }

    public UsuarioModel toUsuarioModel(UsuarioBasicoDTO dto){
        UsuarioModel usuario;
        usuario = mapper.map(dto, UsuarioModel.class);

        CredenciaisModel cred = new CredenciaisModel(usuario, dto.getEmail(), dto.getSenha());
        usuario = mapper.map(cred, UsuarioModel.class);
        
        /* Continuar amanhã:
         * - Descobrir se esse mapeamento da CredenciaisModel deu certo com o mapper e colocar as credenciais no atributo do usuario
         * - Continuar fazendo as rotas do Controller
         */
        return usuario;
    }
}
