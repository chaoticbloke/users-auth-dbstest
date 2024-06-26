package com.phegondev.usersmanagementsystem.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegistrationRequest {
    private String username;
    private String email;
    private String password;
}
