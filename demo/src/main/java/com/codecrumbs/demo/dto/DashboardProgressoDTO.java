package com.codecrumbs.demo.dto;

import java.math.BigDecimal;

import com.codecrumbs.enumeracao.LinguagemEnum;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardProgressoDTO {
    
    @NotBlank
    private BigDecimal percExerciciosConcluidos;

    @NotBlank
    private BigDecimal percQuizzesConcluidos;

    @NotBlank
    private Integer totalFlashCards;

    @NotBlank
    private LinguagemEnum linguagemFavorita;
}
