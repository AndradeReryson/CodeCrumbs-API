package com.codecrumbs.demo.exceptions;

public class InvalidUsuarioException extends RuntimeException {
    
    public InvalidUsuarioException(String msg){
        super(msg);
    }
}
