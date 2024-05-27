package com.codecrumbs.demo.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProgressoQuizCreateDTO {
    
    @NotBlank
    private Integer id_usuario;

    @NotBlank
    private Integer id_quiz;

    @NotBlank
    private BigDecimal nota;
}
