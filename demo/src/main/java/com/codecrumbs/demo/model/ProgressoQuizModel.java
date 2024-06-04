package com.codecrumbs.demo.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "t06_progresso_usuario_quiz")
@Entity(name = "ProgressoQuizModel")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ProgressoQuizModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "A06_Id")
    private Integer id;

    @Column(name = "A06_Nota_Quiz")
    private BigDecimal nota;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "A06_Id_T01_Usuario", nullable = false)
    private UsuarioModel usuario;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "A06_Id_T03_Quiz", nullable = false)
    private QuizModel quiz;

    public ProgressoQuizModel(BigDecimal nota, UsuarioModel usuario, QuizModel quiz) {
        this.nota = nota;
        this.usuario = usuario;
        this.quiz = quiz;
    }
}
