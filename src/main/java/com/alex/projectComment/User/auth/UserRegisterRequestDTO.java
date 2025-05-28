package com.alex.projectComment.User.auth;

public record UserRegisterRequestDTO(String firstName, String lastName, String username, String email, String password) {

}
