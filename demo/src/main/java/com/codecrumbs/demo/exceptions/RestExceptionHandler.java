package com.codecrumbs.demo.exceptions;

import java.util.Date;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ControllerAdvice
public class RestExceptionHandler {
    
    /** EMAIL EM USO */
    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<ApiError> handleEmailAlreadyUsedException() {
        ApiError error = new ApiError(409, "Email já esta em uso", new Date());
        return ResponseEntity.status(409).body(error);
    }

    /** APELIDO EM USO */
    @ExceptionHandler(ApelidoAlreadyUsedException.class)
    public ResponseEntity<ApiError> handleApelidoAlreadyUsedException() {
        ApiError error = new ApiError(409, "Apelido já esta em uso", new Date());
        return ResponseEntity.status(409).body(error);
    }

    /** EMAIL E SENHA INVALIDOS */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentialsException(){
        ApiError error = new ApiError(401, "Email ou Senha invalidos", new Date());
        return ResponseEntity.status(401).body(error);
    }

    /** ID DE USUARIO NÂO EXISTE */
    @ExceptionHandler(InvalidUsuarioException.class)
    public ResponseEntity<ApiError> handleInvalidUsuarioException(){
        ApiError error = new ApiError(404, "Usuario informado nao existe", new Date());
        return ResponseEntity.status(404).body(error);
    }
}
