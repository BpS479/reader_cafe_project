package com.readercafeproject.dto;

import lombok.Data;

@Data
public class UserRegistrationDto {
    private String name;
    private String username;
    // private String rollNo;
    // private String department;
    private String email;
    private String password;
    private String confirmPassword;
}