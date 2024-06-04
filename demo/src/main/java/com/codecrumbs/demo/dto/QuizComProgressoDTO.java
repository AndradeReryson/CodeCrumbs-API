package com.codecrumbs.demo.dto;

import java.math.BigDecimal;
import com.codecrumbs.enumeracao.LinguagemEnum;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Esse é o DTO que traz não só os dados do quiz mas também o progresso do usuário junto, 
 */
@Getter
@Setter
public class QuizComProgressoDTO {

    @NotBlank
    private Integer id;

    @NotBlank
    private String titulo;

    @NotBlank
    private LinguagemEnum linguagem;

    @NotBlank
    private Integer id_criador;

    @NotBlank
    private BigDecimal nota;

}
