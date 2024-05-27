package com.codecrumbs.demo.exceptions;

public class InvalidQuizException extends RuntimeException {
    
    public InvalidQuizException(String msg){
        super(msg);
    }
}
