package com.codecrumbs.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codecrumbs.demo.model.QuizPerguntaModel;

public interface QuizPerguntaRepository extends JpaRepository<QuizPerguntaModel, Integer>{
    
}
