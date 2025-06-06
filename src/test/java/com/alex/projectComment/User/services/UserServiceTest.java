package com.alex.projectComment.User.services;

import com.alex.projectComment.User.auth.AuthLoginResponseDTO;
import com.alex.projectComment.User.auth.AuthService;
import com.alex.projectComment.User.auth.UserRegisterRequestDTO;
import com.alex.projectComment.User.dtos.UserDTO;
import com.alex.projectComment.User.dtos.UserUpdateRequestDTO;
import com.alex.projectComment.User.entities.User;
import com.alex.projectComment.User.repositories.UserRepository;
import com.alex.projectComment.enums.StatusEnum;
import com.alex.projectComment.infra.exceptions.EntityNotFoundException;
import com.alex.projectComment.infra.exceptions.PermissionDeniedException;
import com.alex.projectComment.infra.security.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {

  @Autowired
  private UserService userService;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private TokenService tokenService;
  @Autowired
  private AuthService authService;

  @Autowired
  private HttpServletRequest request;

  @Test
  @Order(1)
  void findAllReturnsActiveUsers() {
    authService.register(new UserRegisterRequestDTO("alex", "Alex", "alexsimao", "alex@email.com", "senha123"));

    Page<UserDTO> result = userService.findAll(PageRequest.of(0, 10));

    assertEquals(1, result.getTotalElements());
    assertEquals("alexsimao", result.getContent().getFirst().getUsername());
  }

  @Test
  @Order(2)
  void findByIdReturnsUserWhenExists() {
    Long id = 1L;
    UserDTO result = userService.findById(id);

    assertEquals(id, result.getId());
    assertEquals("alexsimao", result.getUsername());
  }

  @Test
  @Order(3)
  void findByIdThrowsEntityNotFoundExceptionWhenUserDoesNotExist() {
    assertThrows(EntityNotFoundException.class, () -> userService.findById(999L));
  }

  @Test
  @Order(4)
  void updateUserUpdatesFieldsAndReturnsUpdatedToken() {
    var user = userRepository.findById(1L).get();

    // Simula token válido
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    String token = tokenService.generateKeyToken(user);
    when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + token);

    UserUpdateRequestDTO updateRequest = new UserUpdateRequestDTO("newFirstName", "newLastName", "newUsername", "newEmail@email.com", "newPassword", null);
    AuthLoginResponseDTO result = userService.updateUser(user.getId(), updateRequest, mockRequest);

    assertEquals("newUsername", result.username());
    assertNotNull(result.token());
  }

  @Test
  @Order(5)
  void updateUserThrowsPermissionDeniedExceptionWhenTokenIdDoesNotMatch() {
    authService.register(new UserRegisterRequestDTO("alex2", "Alex2", "alexsimao2", "alex2@email.com", "senha123"));
    var user1 = userRepository.findById(1L).get();
    var user2 = userRepository.findById(2L).get();

    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    String token2 = tokenService.generateKeyToken(user2);
    when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + token2);

    var updateRequest = new UserUpdateRequestDTO("newFirstName2", "newLastName2", "newUsername2", "newEmail2@email.com", "newPassword2", null);
    assertThrows(PermissionDeniedException.class, () -> userService.updateUser(user1.getId(), updateRequest, mockRequest));
  }

  @Test
  @Order(6)
  void deleteUserMarksUserAsDeletedAndReturnsResponse() {
    var user = userRepository.findById(2L).get();

    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    String token = tokenService.generateKeyToken(user);
    when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + token);

    AuthLoginResponseDTO result = userService.deleteUser(user.getId(), mockRequest);

    assertEquals("alexsimao2", result.username());
    assertNull(result.token());
    assertEquals(StatusEnum.DELETED, userRepository.findById(user.getId()).get().getStatus());
  }

  @Test
  @Order(7)
  void deleteUserThrowsPermissionDeniedExceptionWhenTokenIdDoesNotMatch() {
    User user1 = userRepository.findById(1L).get();
    User user2 = userRepository.findById(2L).get();

    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    String token2 = tokenService.generateKeyToken(user2);
    when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + token2);

    assertThrows(PermissionDeniedException.class, () -> userService.deleteUser(user1.getId(), mockRequest));
  }
}