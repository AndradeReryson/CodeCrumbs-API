package com.codecrumbs.demo.exceptions;

public class ApelidoAlreadyUsedException extends RuntimeException {
    
    public ApelidoAlreadyUsedException(String msg){
        super(msg);
    }
}
