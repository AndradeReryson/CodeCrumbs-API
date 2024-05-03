package com.codecrumbs.demo.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "t01_usuario")
@Entity(name = "UsuarioModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class UsuarioModel{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "A01_Id")
    private Integer id;
    
    @Column(name = "A01_Apelido")
    private String apelido;

    @OneToOne(mappedBy = "id_usuario")
    @JsonManagedReference
    private CredenciaisModel credenciais;

    @OneToMany(mappedBy = "criador", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<QuizModel> meus_quizzes;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ProgressoQuizModel> quizzes_concluidos;

    @OneToMany(mappedBy = "criador", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<FlashBaralhoModel> meus_baralhos;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ProgressoExercicioModel> exercicios_concluidos;
}
