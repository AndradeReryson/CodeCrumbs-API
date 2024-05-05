package com.codecrumbs.demo.dto.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.codecrumbs.demo.dto.QuizComPerguntasDTO;
import com.codecrumbs.demo.model.QuizModel;

@Component
public class QuizMapper {
    
    @Autowired
    private ModelMapper mapper;

    public QuizComPerguntasDTO toQuizComPerguntasDTO(QuizModel model){
        QuizComPerguntasDTO dto;

        dto = mapper.map(model, QuizComPerguntasDTO.class);

        return dto;
    }
}
