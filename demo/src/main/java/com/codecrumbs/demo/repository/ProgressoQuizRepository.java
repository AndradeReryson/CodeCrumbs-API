package com.codecrumbs.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.codecrumbs.demo.model.ProgressoQuizModel;

public interface ProgressoQuizRepository extends JpaRepository<ProgressoQuizModel, Integer>{
    
    @Query(value = "SELECT * " +
                    "FROM t06_progresso_usuario_quiz "+
                    "WHERE A06_Id_T03_Quiz = :id_quiz " +
                    "AND A06_Id_T01_Usuario = :id_usuario", nativeQuery = true)
    Optional<ProgressoQuizModel> findByQuizAndUserId(@Param("id_quiz") Integer id_quiz,
                                        @Param("id_usuario") Integer id_usuario);
}
