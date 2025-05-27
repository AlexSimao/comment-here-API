package com.alex.projectComment.auth;

public record UserRegisterRequestDTO(String firstName, String lastName, String username, String email, String password) {

}
