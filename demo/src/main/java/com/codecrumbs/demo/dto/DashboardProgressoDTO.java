package com.codecrumbs.demo.dto;

import com.codecrumbs.enumeracao.LinguagemEnum;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardProgressoDTO {
    
    @NotBlank
    private Float percExerciciosConcluidos;

    @NotBlank
    private Float percQuizzesConcluidos;

    @NotBlank
    private Integer totalFlashCards;

    @NotBlank
    private LinguagemEnum linguagemFavorita;
}
