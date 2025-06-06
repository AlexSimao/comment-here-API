package com.alex.projectComment.Lobby.services;

import com.alex.projectComment.Lobby.dtos.LobbyRequestDTO;
import com.alex.projectComment.Lobby.dtos.LobbyUpdateRequestDTO;
import com.alex.projectComment.Lobby.entities.Lobby;
import com.alex.projectComment.Lobby.repositories.LobbyRepository;
import com.alex.projectComment.User.entities.User;
import com.alex.projectComment.User.repositories.UserRepository;
import com.alex.projectComment.enums.StatusEnum;
import com.alex.projectComment.enums.VisibilityEnum;
import com.alex.projectComment.infra.exceptions.AlreadyInUseException;
import com.alex.projectComment.infra.exceptions.EntityNotFoundException;
import com.alex.projectComment.infra.exceptions.PermissionDeniedException;
import com.alex.projectComment.infra.security.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LobbyServiceTest {

  @Autowired
  private LobbyService lobbyService;
  @Autowired
  private LobbyRepository lobbyRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private TokenService tokenService;

  @BeforeEach
  void setUp() {
    clearRepositories();
  }

  @AfterEach
  void tearDown() {
    clearRepositories();
  }

  private void clearRepositories() {
    lobbyRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  @Order(1)
  void findAllReturnsActiveLobbies() {
    Lobby lobby = new Lobby();
    lobby.setName("Test Lobby");
    lobby.setStatus(StatusEnum.ACTIVE);
    lobbyRepository.save(lobby);

    var result = lobbyService.findAll(PageRequest.of(0, 10));
    assertEquals(1, result.getTotalElements());
  }

  @Test
  @Order(2)
  void findByIdThrowsEntityNotFoundExceptionForInvalidId() {
    assertThrows(EntityNotFoundException.class, () -> lobbyService.findById(999L));
  }

  @Test
  @Order(3)
  void createLobbyThrowsIllegalArgumentExceptionForEmptyName() {
    var request = new LobbyRequestDTO("", List.of("tag1"), List.of("domain1"), VisibilityEnum.PUBLIC, List.of());
    var mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getHeader("Authorization")).thenReturn("Bearer token");
    assertThrows(IllegalArgumentException.class, () -> lobbyService.createLobby(request, mockRequest));
  }

  @Test
  @Order(4)
  void createLobbyPersistsLobbyWithValidData() {
    var user = new User();
    user.setUsername("user1");
    userRepository.save(user);

    var mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + tokenService.generateKeyToken(user));
    var request = new LobbyRequestDTO("Lobby Teste", List.of("tag1"), List.of("domain1"), VisibilityEnum.PUBLIC, List.of(user.getId()));
    var dto = lobbyService.createLobby(request, mockRequest);
    assertEquals("Lobby Teste", dto.getName());
    assertEquals(user.getId(), dto.getCreator().getId());
  }

  @Test
  @Order(5)
  void updateLobbyThrowsEntityNotFoundExceptionForInvalidLobbyId() {
    var updateDTO = new LobbyUpdateRequestDTO("NovoNome", List.of("tag1"), List.of("domain1"), VisibilityEnum.PUBLIC);
    var mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getHeader("Authorization")).thenReturn("Bearer token");
    assertThrows(EntityNotFoundException.class, () -> lobbyService.updateLobby(12345L, updateDTO, mockRequest));
  }

  @Test
  @Order(6)
  void deleteLobbyMarksLobbyAsDeletedWhenUserIsUserPrime() {
    var user = new User();
    user.setUsername("userPrime");
    userRepository.save(user);

    var lobby = new Lobby();
    lobby.setName("LobbyDel");
    lobby.setStatus(StatusEnum.ACTIVE);
    lobby.setUserPrime(user);
    lobbyRepository.save(lobby);

    var mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + tokenService.generateKeyToken(user));
    lobbyService.deleteLobby(lobby.getId(), mockRequest);
    assertEquals(StatusEnum.DELETED, lobbyRepository.findById(lobby.getId()).orElseThrow().getStatus());
  }

  @Test
  @Order(7)
  void deleteLobbyThrowsIllegalArgumentExceptionWhenTokenIsNull() {
    var lobby = new Lobby();
    lobby.setName("LobbyDel");
    lobby.setStatus(StatusEnum.ACTIVE);
    lobbyRepository.save(lobby);

    var mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getHeader("Authorization")).thenReturn(null);
    assertThrows(IllegalArgumentException.class, () -> lobbyService.deleteLobby(lobby.getId(), mockRequest));
  }

  @Test
  @Order(8)
  void createLobbyThrowsAlreadyInUseExceptionForDuplicateName() {
    var user = new User();
    user.setUsername("user2");
    userRepository.save(user);

    var mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + tokenService.generateKeyToken(user));
    var request = new LobbyRequestDTO("Duplicado", List.of("tag1"), List.of("domain1"), VisibilityEnum.PUBLIC, List.of(user.getId()));
    lobbyService.createLobby(request, mockRequest);

    var request2 = new LobbyRequestDTO("Duplicado", List.of("tag2"), List.of("domain2"), VisibilityEnum.PUBLIC, List.of(user.getId()));
    assertThrows(AlreadyInUseException.class, () -> lobbyService.createLobby(request2, mockRequest));
  }

  @Test
  @Order(9)
  void createLobbyThrowsIllegalArgumentExceptionWhenNoTags() {
    var user = new User();
    user.setUsername("user3");
    userRepository.save(user);

    var mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + tokenService.generateKeyToken(user));
    var request = new LobbyRequestDTO("LobbySemTag", List.of(), List.of("domain1"), VisibilityEnum.PUBLIC, List.of(user.getId()));
    assertThrows(IllegalArgumentException.class, () -> lobbyService.createLobby(request, mockRequest));
  }

  @Test
  @Order(10)
  void updateLobbyThrowsPermissionDeniedExceptionIfNotAdmin() {
    var user = new User();
    user.setUsername("adminUser");
    userRepository.save(user);

    var lobby = new Lobby();
    lobby.setName("LobbyUpdate");
    lobby.setStatus(StatusEnum.ACTIVE);
    lobby.setUserPrime(user);
    lobby.setUsersAdmin(List.of(user));
    lobbyRepository.save(lobby);

    var notAdmin = new User();
    notAdmin.setUsername("notAdmin");
    userRepository.save(notAdmin);

    var mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + tokenService.generateKeyToken(notAdmin));
    var updateDTO = new LobbyUpdateRequestDTO("NovoNome", List.of("tag1"), List.of("domain1"), VisibilityEnum.PUBLIC);

    assertThrows(PermissionDeniedException.class, () -> lobbyService.updateLobby(lobby.getId(), updateDTO, mockRequest));
  }

  @Test
  @Order(11)
  void deleteLobbyThrowsPermissionDeniedExceptionIfNotUserPrime() {
    var user = new User();
    user.setUsername("primeUser");
    userRepository.save(user);

    var lobby = new Lobby();
    lobby.setName("LobbyDelete");
    lobby.setStatus(StatusEnum.ACTIVE);
    lobby.setUserPrime(user);
    lobbyRepository.save(lobby);

    var notPrime = new User();
    notPrime.setUsername("notPrime");
    userRepository.save(notPrime);

    var mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + tokenService.generateKeyToken(notPrime));

    assertThrows(PermissionDeniedException.class, () -> lobbyService.deleteLobby(lobby.getId(), mockRequest));
  }
}