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

@Table(name = "t10_progresso_usuario_exercicio")
@Entity(name = "ProgressoExercicioModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ProgressoExercicioModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "A10_Id")
    private Integer id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "A10_Id_T01_Usuario")
    private UsuarioModel usuario;
}
