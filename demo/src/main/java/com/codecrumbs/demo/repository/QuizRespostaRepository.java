package com.codecrumbs.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codecrumbs.demo.model.QuizRespostaModel;

public interface QuizRespostaRepository extends JpaRepository<QuizRespostaModel, Integer>{
    
}
