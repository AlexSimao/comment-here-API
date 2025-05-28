package com.alex.projectComment.User.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<AuthLoginResponseDTO> login(@RequestBody UserLoginRequestDTO userLoginRequestDTO) {
    AuthLoginResponseDTO result = authService.login(userLoginRequestDTO);
    return ResponseEntity.ok(result);
  }

  @PostMapping("/register")
  public ResponseEntity<AuthLoginResponseDTO> register(@RequestBody UserRegisterRequestDTO userRegisterRequestDTO) {
    AuthLoginResponseDTO result = authService.register(userRegisterRequestDTO);
    return ResponseEntity.ok(result);
  }

}