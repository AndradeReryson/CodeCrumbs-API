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

@Table(name = "t07_flashcard_baralho")
@Entity(name = "FlashBaralhoModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class FlashBaralhoModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "A07_Id")
    private Integer id;

    @Column(name = "A07_Titulo")
    private String titulo;

    @Column(name = "A07_Cor")
    private String cor;

    @Column(name = "A07_Linguagem")
    private LinguagemEnum linguagem;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "A07_Criador_T01_Usuario", nullable = false)
    private UsuarioModel criador;

    @OneToMany(mappedBy = "baralho_pai", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<FlashCartaoModel> lista_cartoes;
}
