package com.codecrumbs.demo.model;

import java.util.List;

import com.codecrumbs.enumeracao.LinguagemEnum;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
import lombok.Setter;

@Table(name = "t03_quiz")
@Entity(name = "QuizModel")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class QuizModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "A03_Id")
    private Integer id;

    @Column(name = "A03_Titulo", length = 100)
    private String titulo;

    @Column(name = "A03_Linguagem")
    private LinguagemEnum linguagem;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "A03_Criador_T01_Usuario", nullable = false)
    private UsuarioModel criador;

    @OneToMany(mappedBy = "quiz_pai", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<QuizPerguntaModel> lista_perguntas;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ProgressoQuizModel> lista_quizzes; 

    /* */

    public QuizModel(   String titulo,
                        LinguagemEnum linguagem,
                        UsuarioModel criador){
        this.titulo = titulo;
        this.linguagem = linguagem;
        this.criador = criador;                       
    }
}
