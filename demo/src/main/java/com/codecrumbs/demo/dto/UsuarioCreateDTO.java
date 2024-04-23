package com.codecrumbs.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioCreateDTO {
    
    @NotBlank(message = "Insira um apelido para sua conta")
    @Size(min = 4, max = 20, message = "Apelido deve ter no mínimo 4 caracteres e no máximo 20 caracteres")
    private String apelido;

    @NotBlank(message = "Insira um endereço de email")
    private String email;

    @NotBlank(message = "Insira uma senha")
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
    private String senha;
}
