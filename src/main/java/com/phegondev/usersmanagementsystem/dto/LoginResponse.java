package com.phegondev.usersmanagementsystem.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private int statusCode;
    private String error;
    private String message;
    private Integer loginAttempts;
    private String token;
    private String expirationTime;
}
