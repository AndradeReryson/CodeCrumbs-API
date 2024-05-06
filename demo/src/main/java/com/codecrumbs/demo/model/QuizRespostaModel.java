package com.codecrumbs.demo.model;

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

@Table(name = "t05_quiz_respostas")
@Entity(name = "QuizRespostaModel")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class QuizRespostaModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "A05_Id")
    private Integer id;

    @Column(name = "A05_Texto_Resposta")
    private String texto;

    @Column(name = "A05_is_Resposta_Correta")
    private Boolean isCorreta;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "A05_Id_T04_Quiz_Perguntas", nullable = false)
    private QuizPerguntaModel pergunta_pai;

    /* */

    public QuizRespostaModel(   String texto,
                                Boolean isCorreta,
                                QuizPerguntaModel pergunta_pai){
        this.texto = texto;
        this.isCorreta = isCorreta;
        this.pergunta_pai = pergunta_pai;
    }
}
