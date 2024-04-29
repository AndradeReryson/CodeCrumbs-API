package com.codecrumbs.demo.exceptions;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiError {
    private Integer errorCode;
    private String errorMessage;
    private Date errorDate;
}
