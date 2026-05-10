package com.cloudops.authservice.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class TokenRequest {

    @NotBlank
    private String username;

    private List<String> roles;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
