package com.codecrumbs.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codecrumbs.demo.model.ProgressoQuizModel;

public interface ProgressoQuizRepository extends JpaRepository<ProgressoQuizModel, Integer>{
    
    
}
