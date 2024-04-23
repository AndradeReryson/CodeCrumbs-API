package com.codecrumbs.demo.model;

import com.codecrumbs.enumeracao.LinguagemEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "t09_exercicio")
@Entity(name = "ExercicioModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ExercicioModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "A09_Id")
    private Integer id;

    @Column(name = "A09_Linguagem")
    private LinguagemEnum linguagem;

    @Column(name = "A09_Enunciado")
    private String enunciado;

    @Column(name = "A09_Texto_Codigo", columnDefinition="TEXT")
    private String texto_codigo;

    @Column(name = "A09_Resposta")
    private String resposta;
}
