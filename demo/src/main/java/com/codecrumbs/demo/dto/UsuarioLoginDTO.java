package com.codecrumbs.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioLoginDTO {
    
    @NotBlank(message = "Informe um email")
    private String email;

    @NotBlank(message = "Informe uma senha")
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
    private String senha;
}
