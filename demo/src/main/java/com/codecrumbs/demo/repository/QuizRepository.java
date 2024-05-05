package com.codecrumbs.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.codecrumbs.demo.model.QuizModel;
import java.util.Optional;


public interface QuizRepository extends JpaRepository<QuizModel, Integer>{
    
    Optional<QuizModel> findById(Integer id);

    @Query(value = "CALL proc_buscar_quizzes_ref_usuario(:id)", nativeQuery = true)
    Page<QuizModel> findAllQuizzesAndProgress(@Param("id") Integer id, Pageable pageable);
}
