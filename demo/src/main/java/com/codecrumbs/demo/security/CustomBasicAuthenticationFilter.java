package com.codecrumbs.demo.security;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;


import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.codecrumbs.demo.model.UsuarioModel;
import com.codecrumbs.demo.repository.UsuarioRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomBasicAuthenticationFilter extends OncePerRequestFilter{
    private static final String AUTHORIZATION = "Authorization";
    private static final String BASIC = "Basic";

    private final UsuarioRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if(isBasicAuthentication(request)){
            String[] credentials = decodeBase64(getHeader(request).replace(BASIC, "")).split(":");
            
            String username = credentials[0];
            String password = credentials[1];

            Optional<UsuarioModel> opt_usuario = repository.fazerLogin(username, password);

            /* 
            if(opt_usuario.isEmpty()){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Usuário Invalido");
                return;
            }
            */

            setAuthentication(opt_usuario.get());
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(UsuarioModel usuario) {
        Authentication authentication = createAuthenticationToken(usuario);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Authentication createAuthenticationToken(UsuarioModel usuario) {
        UsuarioPrincipal usuarioPrincipal = UsuarioPrincipal.create(usuario);
        return new UsernamePasswordAuthenticationToken(usuarioPrincipal, null, null);
    }

    private String decodeBase64(String base64) {
        byte[] decodeBytes = Base64.getMimeDecoder().decode(base64);
        return new String(decodeBytes);
    }

    private Boolean isBasicAuthentication(HttpServletRequest request){
        String header = getHeader(request);
        return header != null && header.startsWith(BASIC);
    }

    private String getHeader(HttpServletRequest request){
        return request.getHeader(AUTHORIZATION);
    }
    
}
