package com.alex.projectComment.Section.services;

import com.alex.projectComment.Lobby.entities.Lobby;
import com.alex.projectComment.Lobby.repositories.LobbyRepository;
import com.alex.projectComment.Section.dtos.SectionDTO;
import com.alex.projectComment.Section.dtos.SectionRequestDTO;
import com.alex.projectComment.Section.dtos.SectionUpdateRequestDTO;
import com.alex.projectComment.Section.entities.Section;
import com.alex.projectComment.Section.repositories.SectionRepository;
import com.alex.projectComment.User.entities.User;
import com.alex.projectComment.User.repositories.UserRepository;
import com.alex.projectComment.enums.StatusEnum;
import com.alex.projectComment.infra.exceptions.AlreadyInUseException;
import com.alex.projectComment.infra.exceptions.EntityNotFoundException;
import com.alex.projectComment.infra.exceptions.PermissionDeniedException;
import com.alex.projectComment.infra.security.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class SectionServiceTest {
  @Autowired
  private SectionRepository sectionRepository;
  @Autowired
  private LobbyRepository lobbyRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private SectionService sectionService;
  @Autowired
  private TokenService tokenService;

  @BeforeEach
  void setUp() {
    sectionRepository.deleteAll();
    lobbyRepository.deleteAll();
    userRepository.deleteAll();
  }

  @AfterEach
  void tearDown() {
    sectionRepository.deleteAll();
    lobbyRepository.deleteAll();
    userRepository.deleteAll();
  }

  private User createUser(String username) {
    User user = new User();
    user.setUsername(username);
    user.setPassword("test123");
    user.setEmail(username + "@email.com");
    return userRepository.save(user);
  }

  private Lobby createLobby(String name, User user) {
    Lobby lobby = new Lobby();
    lobby.setName(name);
    lobby.setUserPrime(user);
    lobby.setCreator(user);
    lobby.setUsersAdmin(List.of(user));
    lobby.setStatus(StatusEnum.ACTIVE);
    lobby.setCreationDate(LocalDateTime.now());
    return lobbyRepository.save(lobby);
  }

  private Section createSection(String name, Lobby lobby, User user) {
    Section section = new Section();
    section.setName(name);
    section.setLobby(lobby);
    section.setStatus(StatusEnum.ACTIVE);
    section.setCreatedBy(user);
    return sectionRepository.save(section);
  }

  private HttpServletRequest mockRequest(User user) {
    HttpServletRequest request = mock(HttpServletRequest.class);
    String token = tokenService.generateKeyToken(user);
    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
    return request;
  }

  @Test
  void shouldReturnOnlyActiveSections() {
    User user = createUser("user1");
    Lobby lobby = createLobby("Lobby1", user);
    createSection("SecActive", lobby, user);
    Section deleted = createSection("SecDeleted", lobby, user);
    deleted.setStatus(StatusEnum.DELETED);
    sectionRepository.save(deleted);

    Pageable pageable = PageRequest.of(0, 10);
    Page<Section> sections = sectionRepository.findAllByStatusNot(StatusEnum.DELETED, pageable);

    assertTrue(sections.stream().anyMatch(s -> s.getName().equals("SecActive")));
    assertFalse(sections.stream().anyMatch(s -> s.getName().equals("SecDeleted")));
  }

  @Test
  void shouldReturnSectionById() {
    User user = createUser("user1");
    Lobby lobby = createLobby("LobbyTeste", user);
    Section section = createSection("SecaoTeste", lobby, user);

    SectionDTO found = sectionService.findById(section.getId());

    assertEquals(section.getId(), found.getId());
    assertEquals("SecaoTeste", found.getName());
  }

  @Test
  void shouldReturnSectionByName() {
    User user = createUser("user1");
    Lobby lobby = createLobby("LobbyTeste", user);
    createSection("SecaoPorNome", lobby, user);

    SectionDTO found = sectionService.findByName("SecaoPorNome");
    assertEquals("SecaoPorNome", found.getName());
  }

  @Test
  void shouldCreateNewSection() {
    User user = createUser("user1");
    Lobby lobby = createLobby("NovaSecaoLobby", user);
    SectionRequestDTO requestDTO = new SectionRequestDTO("NovaSecao");

    SectionDTO created = sectionService.createSection(lobby.getId(), requestDTO, mockRequest(user));

    assertNotNull(created.getId());
    assertEquals("NovaSecao", created.getName());
    assertEquals(StatusEnum.ACTIVE, created.getStatus());
  }

  @Test
  void shouldNotCreateSectionWithExistingName() {
    User user = createUser("user1");
    Lobby lobby = createLobby("Lobby1", user);
    SectionRequestDTO requestDTO = new SectionRequestDTO("SecaoDuplicada");
    sectionService.createSection(lobby.getId(), requestDTO, mockRequest(user));

    assertThrows(AlreadyInUseException.class, () ->
        sectionService.createSection(lobby.getId(), requestDTO, mockRequest(user))
    );
  }

  @Test
  void shouldNotCreateSectionWithoutPermission() {
    User admin = createUser("admin");
    User other = createUser("other");
    Lobby lobby = createLobby("Lobby1", admin);
    SectionRequestDTO requestDTO = new SectionRequestDTO("SecaoSemPermissao");

    assertThrows(PermissionDeniedException.class, () ->
        sectionService.createSection(lobby.getId(), requestDTO, mockRequest(other))
    );
  }

  @Test
  void shouldNotCreateSectionWithNullOrBlankName() {
    User user = createUser("user1");
    Lobby lobby = createLobby("Lobby1", user);

    SectionRequestDTO nullName = new SectionRequestDTO(null);
    SectionRequestDTO blankName = new SectionRequestDTO("   ");

    assertThrows(IllegalArgumentException.class, () ->
        sectionService.createSection(lobby.getId(), nullName, mockRequest(user))
    );
    assertThrows(IllegalArgumentException.class, () ->
        sectionService.createSection(lobby.getId(), blankName, mockRequest(user))
    );
  }

  @Test
  void shouldThrowWhenSectionNotFoundById() {
    assertThrows(EntityNotFoundException.class, () ->
        sectionService.findById(9999L)
    );
  }

  @Test
  void shouldThrowWhenSectionNotFoundByName() {
    assertThrows(EntityNotFoundException.class, () ->
        sectionService.findByName("Inexistente")
    );
  }

  @Test
  void shouldNotUpdateSectionToExistingName() {
    User user = createUser("user1");
    Lobby lobby = createLobby("Lobby1", user);
    Section section1 = createSection("Secao1", lobby, user);
    createSection("Secao2", lobby, user);

    var updateDTO = new SectionUpdateRequestDTO("Secao2", null);
    assertThrows(IllegalArgumentException.class, () ->
        sectionService.updateSection(section1.getId(), updateDTO, mockRequest(user))
    );
  }

  @Test
  void shouldNotUpdateSectionStatusToDeleted() {
    User user = createUser("user1");
    Lobby lobby = createLobby("Lobby1", user);
    Section section = createSection("Secao", lobby, user);

    var updateDTO = new SectionUpdateRequestDTO("other", StatusEnum.DELETED);
    assertThrows(IllegalArgumentException.class, () ->
        sectionService.updateSection(section.getId(), updateDTO, mockRequest(user))
    );
  }
}