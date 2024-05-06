package com.codecrumbs.demo.dto;

import java.util.List;

import com.codecrumbs.enumeracao.LinguagemEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizCreateDTO {
    
    private String titulo;

    private LinguagemEnum linguagem;

    private Integer id_criador;

    private List<QuizPerguntaCreateDTO> lista_perguntas;
}
