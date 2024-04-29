package com.codecrumbs.demo.exceptions;

public class EmailAlreadyUsedException extends RuntimeException {
    
    public EmailAlreadyUsedException(String msg){
        super(msg);
    }
}
