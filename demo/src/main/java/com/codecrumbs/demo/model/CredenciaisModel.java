package com.codecrumbs.demo.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "t02_credenciais")
@Entity(name = "CredenciaisModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class CredenciaisModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "A02_Id")
    private Integer id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "A02_Id_T01_Usuario", nullable = false)
    private UsuarioModel id_usuario;

    @Column(name = "A02_Email", length = 255)
    private String email;

    @Column(name = "A02_Senha", length = 64)
    private String senha;

    public CredenciaisModel(UsuarioModel id_usuario, String email, String senha){
        this.id_usuario = id_usuario;
        this.email = email;
        this.senha = senha;
    }
}
