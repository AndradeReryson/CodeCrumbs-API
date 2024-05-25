package com.codecrumbs.demo.security;



import com.codecrumbs.demo.model.UsuarioModel;

import lombok.Getter;

@Getter
public class UsuarioPrincipal{
    private String username; // É o email 
    private String password;

    
    private UsuarioPrincipal(UsuarioModel model){
        this.username = model.getCredenciais().getEmail();
        this.password = model.getCredenciais().getSenha();
    }

    public static UsuarioPrincipal create(UsuarioModel model){
        return new UsuarioPrincipal(model);
    }

}
