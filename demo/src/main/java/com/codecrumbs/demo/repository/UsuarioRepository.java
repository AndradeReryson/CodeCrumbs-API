package com.codecrumbs.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.codecrumbs.demo.model.UsuarioModel;

public interface UsuarioRepository extends JpaRepository<UsuarioModel, Integer>{
    
    @Query(value = "CALL proc_buscar_usuario_por_email(:email)", nativeQuery = true)
    Optional<UsuarioModel> encontrarPorEmail(@Param("email" ) String email);

    @Query(value = "CALL proc_cadastrar_usuario(:email, :senha, :apelido)", nativeQuery = true)
    Optional<UsuarioModel> cadastrarUsuario(@Param("email") String email, 
                                            @Param("senha") String senha,
                                            @Param("apelido") String apelido);

    @Query(value = "CALL proc_fazer_login(:email, :senha)", nativeQuery = true)
    Optional<UsuarioModel> fazerLogin(@Param("email") String email,
                                        @Param("senha") String senha);

}
