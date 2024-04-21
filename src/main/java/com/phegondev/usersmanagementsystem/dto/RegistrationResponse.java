package com.phegondev.usersmanagementsystem.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationResponse {
    private int statusCode;
    private String error;
    private String message;
    private Integer loginAttempts;
    private String token;
    private String refreshToken;
    private String expirationTime;
    private String username;
    private String role;
    private String email;
    private String password;
}
