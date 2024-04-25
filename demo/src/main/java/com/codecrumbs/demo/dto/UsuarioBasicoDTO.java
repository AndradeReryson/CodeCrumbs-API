package com.codecrumbs.demo.dto;

import com.codecrumbs.demo.model.CredenciaisModel;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioBasicoDTO {
    
    @NotBlank
    private Integer id;

    @NotBlank
    private String apelido;

    @NotBlank
    private String email;

    @NotBlank
    private String senha;
}
