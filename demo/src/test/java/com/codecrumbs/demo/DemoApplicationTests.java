package com.codecrumbs.demo;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.codecrumbs.demo.controller.QuizController;
import com.codecrumbs.demo.dto.QuizCreateDTO;
import com.codecrumbs.enumeracao.LinguagemEnum;

@SpringBootTest
class DemoApplicationTests {

	@Autowired
	private QuizController quizController;

	@Test
	void contextLoads() {
		QuizCreateDTO dto = new QuizCreateDTO();
		dto.setTitulo("QUIZ TUTORIAL");
		dto.setLinguagem(LinguagemEnum.CSS);
		dto.setLista_perguntas(List.of());
		dto.setId_criador(1);
		//
		quizController.criarNovoQuiz(dto);
	}

}
