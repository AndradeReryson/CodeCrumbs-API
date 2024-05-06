package com.codecrumbs.demo.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizPerguntaCreateDTO {
    
    private String enunciado;

    private List<QuizRespostaCreateDTO> lista_respostas;
}
