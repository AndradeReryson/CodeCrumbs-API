package com.codecrumbs.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizRespostaCreateDTO {
    
    private String texto;

    private Boolean isCorreta;
}
