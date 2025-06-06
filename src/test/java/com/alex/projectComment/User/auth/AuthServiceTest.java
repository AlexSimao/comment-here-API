package com.alex.projectComment.User.auth;

import com.alex.projectComment.User.repositories.UserRepository;
import com.alex.projectComment.infra.exceptions.AlreadyInUseException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthServiceTest {
  @Autowired
  private AuthService authService;
  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
  }

  @Test
  @Order(1)
  void loginWithActiveUserAndValidPasswordReturnsToken() {
    authService.register(new UserRegisterRequestDTO("alex", "Alex", "alexsimao", "alex@email.com", "senha123"));
    var response = authService.login(new UserLoginRequestDTO("alexsimao", "senha123"));

    assertEquals("alexsimao", response.username());
    assertNotNull(response.token());
  }

  @Test
  @Order(2)
  void loginWithIncorrectPasswordThrowsIllegalArgumentException() {
    authService.register(new UserRegisterRequestDTO("alex", "Alex", "alexsimao", "alex@email.com", "senha123"));
    assertThrows(IllegalArgumentException.class, () -> authService.login(new UserLoginRequestDTO("alexsimao", "senha1234")));
  }

  @Test
  @Order(3)
  void registerWithExistingEmailThrowsAlreadyInUseException() {
    authService.register(new UserRegisterRequestDTO("alex", "Alex", "alexsimao", "alex@email.com", "senha123"));
    assertThrows(AlreadyInUseException.class, () -> authService.register(new UserRegisterRequestDTO("alex", "Alex", "alexsimao", "alex@email.com", "senha123")));
  }

  @Test
  @Order(4)
  void registerWithExistingUsernameThrowsAlreadyInUseException() {
    authService.register(new UserRegisterRequestDTO("alex", "Alex", "alexsimao", "alex@email.com", "senha123"));
    assertThrows(AlreadyInUseException.class, () -> authService.register(new UserRegisterRequestDTO("alex", "Alex", "alexsimao", "souza@email.com", "senha123")));
  }

  @Test
  @Order(5)
  void registerWithShortPasswordThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> authService.register(new UserRegisterRequestDTO("alex3", "Alex", "Simao1", "alex1@email.com", "123")));
  }

  @Test
  @Order(6)
  void registerWithBlankEmailThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> authService.register(new UserRegisterRequestDTO("alex", "Alex", "userblank", "", "senha123")));
  }

  @Test
  @Order(7)
  void registerWithBlankUsernameThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> authService.register(new UserRegisterRequestDTO("alex", "Alex", "", "blank@email.com", "senha123")));
  }

  @Test
  @Order(8)
  void registerWithNullPasswordThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> authService.register(new UserRegisterRequestDTO("alex", "Alex", "nullpass", "null@email.com", null)));
  }
}