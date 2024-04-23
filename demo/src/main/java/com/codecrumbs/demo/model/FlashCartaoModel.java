package com.codecrumbs.demo.model;

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

@Table(name = "t08_flashcard_cartao")
@Entity(name = "FlashCartaoModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class FlashCartaoModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "A08_Id")
    private Integer id;

    @Column(name = "A08_Termo")
    private String frente_termo;

    @Column(name = "A08_Definicao")
    private String verso_definicao;

    @ManyToOne
    @JoinColumn(name = "A08_Id_T07_Flashcard_baralho", nullable = false)
    private FlashBaralhoModel baralho_pai;
}
