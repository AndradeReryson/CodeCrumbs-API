package com.codecrumbs.demo.dto;

import java.util.List;

import com.codecrumbs.demo.model.QuizPerguntaModel;
import com.codecrumbs.enumeracao.LinguagemEnum;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuizComPerguntasDTO {
    
    @NotBlank
    private Integer id;

    @NotBlank
    private String titulo;

    @NotBlank
    private LinguagemEnum linguagem;

    @NotBlank
    private List<QuizPerguntaModel> lista_perguntas;
}
