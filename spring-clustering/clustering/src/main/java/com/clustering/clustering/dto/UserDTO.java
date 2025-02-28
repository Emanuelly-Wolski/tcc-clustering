package com.clustering.clustering.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDTO {
    
    private String name;
    private String email;
    private String role; // Agora como String

    public UserDTO() {
    }

    public UserDTO(String name, String email, String role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("role")
    public String getRole() {
        return role;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }
}