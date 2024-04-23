package com.codecrumbs.demo.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "t04_quiz_perguntas")
@Entity(name = "QuizPerguntaModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class QuizPerguntaModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "A04_Id")
    private Integer id;

    @Column(name = "A04_Enunciado")
    private String enunciado;

    @ManyToOne
    @JoinColumn(name = "A04_Id_T03_Quiz", nullable = false)
    private QuizModel quiz_pai;
    
    @OneToMany(mappedBy = "pergunta_pai", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizRespostaModel> lista_respostas;
    
}
