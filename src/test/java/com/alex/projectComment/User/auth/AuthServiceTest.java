package com.alex.projectComment.User.auth;

import com.alex.projectComment.infra.exceptions.AlreadyInUseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceTest {
  @Autowired
  private AuthService authService;

  @Test
  void loginWithActiveUserAndValidPasswordReturnsToken() {
    authService.register(new UserRegisterRequestDTO("alex", "Alex", "alexsimao", "alex@email.com", "senha123"));
    var response = authService.login(new UserLoginRequestDTO("alexsimao", "senha123"));

    assertEquals("alexsimao", response.username());
    assertNotNull(response.token());
  }

  @Test
  void loginWithIncorrectPasswordThrowsIllegalArgumentException() {
    authService.register(new UserRegisterRequestDTO("alex", "Alex", "souza3", "alex3@email.com", "senha123"));
    assertThrows(IllegalArgumentException.class, () -> authService.login(new UserLoginRequestDTO("souza3", "senha1234")));
  }

  @Test
  void registerWithExistingEmailThrowsAlreadyInUseException() {
    assertThrows(AlreadyInUseException.class, () -> authService.register(new UserRegisterRequestDTO("alex", "Alex", "souza3", "alex@email.com", "senha123")));
  }

  @Test
  void registerWithExistingUsernameThrowsAlreadyInUseException() {
    assertThrows(AlreadyInUseException.class, () -> authService.register(new UserRegisterRequestDTO("alex", "Alex", "alexsimao", "souza@email.com", "senha123")));
  }

  @Test
  void registerWithShortPasswordThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> authService.register(new UserRegisterRequestDTO("alex3", "Alex", "Simao1", "alex1@email.com", "123")));
  }

  @Test
  void registerWithBlankEmailThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> authService.register(new UserRegisterRequestDTO("alex", "Alex", "userblank", "", "senha123")));
  }

  @Test
  void registerWithBlankUsernameThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> authService.register(new UserRegisterRequestDTO("alex", "Alex", "", "blank@email.com", "senha123")));
  }

  @Test
  void registerWithNullPasswordThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> authService.register(new UserRegisterRequestDTO("alex", "Alex", "nullpass", "null@email.com", null)));
  }
}