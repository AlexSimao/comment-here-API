package com.alex.projectComment.Lobby.services;

import com.alex.projectComment.Lobby.dtos.*;
import com.alex.projectComment.Lobby.entities.Lobby;
import com.alex.projectComment.Lobby.mappers.DomainMapper;
import com.alex.projectComment.Lobby.mappers.LobbyMapper;
import com.alex.projectComment.Lobby.mappers.TagMapper;
import com.alex.projectComment.Lobby.repositories.LobbyRepository;
import com.alex.projectComment.User.entities.User;
import com.alex.projectComment.User.repositories.UserRepository;
import com.alex.projectComment.enums.StatusEnum;
import com.alex.projectComment.infra.exceptions.AlreadyInUseException;
import com.alex.projectComment.infra.exceptions.EntityNotFoundException;
import com.alex.projectComment.infra.exceptions.PermissionDeniedException;
import com.alex.projectComment.infra.security.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LobbyService {

  @Autowired
  private LobbyRepository lobbyRepository;
  @Autowired
  private TokenService tokenService;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private LobbyMapper lobbyMapper;
  @Autowired
  private TagMapper tagMapper;
  @Autowired
  private TagService tagService;
  @Autowired
  private DomainService domainService;
  @Autowired
  private DomainMapper domainMapper;

  @Transactional(readOnly = true)
  public Page<LobbyDTO> findAll(Pageable pageable) {
    Page<Lobby> result = lobbyRepository.findAllByStatus(StatusEnum.ACTIVE, pageable);
    return result.map(lobbyMapper::toDTO);
  }

  @Transactional(readOnly = true)
  public LobbyDTO findById(Long id) {
    Lobby result = lobbyRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Lobby com o id: " + id + " não encontrado."));
    return lobbyMapper.toDTO(result);
  }

  @Transactional
  public LobbyDTO createLobby(LobbyRequestDTO lobbyRequestDTO, HttpServletRequest request) {
    if (lobbyRequestDTO.name().isBlank()) {
      throw new IllegalArgumentException("Nome do Lobby não pode ser vazio.");
    }

    if (lobbyRepository.existsByNameAndStatusIgnoreCase(lobbyRequestDTO.name(), StatusEnum.ACTIVE)) {
      throw new AlreadyInUseException("Ja existe um Lobby com esse nome: " + lobbyRequestDTO.name());
    }

    if (lobbyRequestDTO.tags().isEmpty()) {
      throw new IllegalArgumentException("O Lobby deve conter ao menos uma(1) tag. Assim facilitando o lobby ser encontrado.");
    }

    List<TagDTO> tags = lobbyRequestDTO.tags().stream()
        .map(tagName -> tagService.existsByName(tagName)
            ? tagService.findByName(tagName)
            : tagService.createTag(tagName))
        .toList();

    List<DomainDTO> domains = lobbyRequestDTO.domains().stream()
        .map(domainName -> domainService.existsByName(domainName)
            ? domainService.findByName(domainName)
            : domainService.createDomain(domainName))
        .toList();

    String token = tokenService.recoverToken(request);
    User sectionUser = userRepository.findById(tokenService.getTokenId(token)).orElseThrow(() -> new EntityNotFoundException("Token de sessão invalido."));

    List<User> admins = lobbyRequestDTO.usersAdminIds().stream()
        .map(userId -> userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("O Admin com id: " + userId + " não existe.")))
        .toList();

    if (!admins.contains(sectionUser)) {
      admins.add(sectionUser);
    }

    Lobby lobby = new Lobby();


    lobby.setDomains(domainMapper.listDTOToListEntity(domains));
    lobby.setName(lobbyRequestDTO.name());
    lobby.setTags(tagMapper.listDTOToListEntity(tags));
    lobby.setVisibility(lobbyRequestDTO.visibility());
    lobby.setCreationDate(LocalDateTime.now());
    lobby.setStatus(StatusEnum.ACTIVE);

    lobby.setCreator(sectionUser);
    lobby.setUserPrime(sectionUser);
    lobby.setUsersAdmin(admins);

    lobby = lobbyRepository.save(lobby);
    return lobbyMapper.toDTO(lobby);
  }

  @Transactional
  public LobbyDTO updateLobby(Long id, LobbyUpdateRequestDTO lobbyRequestDTO, HttpServletRequest request) {
//    Admins não podem adicionar ou remover outros admins, apenas o User Prime do Lobby pode fazer isso.
    Lobby lobby = lobbyRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Lobby com o id: " + id + " não encontrado."));

    String token = tokenService.recoverToken(request);
    User sectionUser = userRepository.findById(tokenService.getTokenId(token))
        .orElseThrow(() -> new EntityNotFoundException("Token de sessão invalido."));

    if (!lobby.getUsersAdmin().contains(sectionUser)) {
      throw new PermissionDeniedException("O Usuário " + sectionUser.getUsername() + " não é um administrador deste Lobby.");
    }

    if (lobbyRepository.existsByNameAndStatusIgnoreCase(lobbyRequestDTO.name(), StatusEnum.ACTIVE)) {
      throw new AlreadyInUseException("Ja existe um Lobby com esse nome: " + lobbyRequestDTO.name());
    }

    List<TagDTO> tags = lobbyRequestDTO.tags().stream()
        .map(tagName -> tagService.existsByName(tagName)
            ? tagService.findByName(tagName)
            : tagService.createTag(tagName))
        .toList();

    List<DomainDTO> domains = lobbyRequestDTO.domains().stream()
        .map(domainName -> domainService.existsByName(domainName)
            ? domainService.findByName(domainName)
            : domainService.createDomain(domainName))
        .toList();

    lobby.setDomains(getNewOrDefault(domainMapper.listDTOToListEntity(domains), lobby.getDomains()));
    lobby.setName(getNewOrDefault(lobbyRequestDTO.name(), lobby.getName()));
    lobby.setTags(getNewOrDefault(tagMapper.listDTOToListEntity(tags), lobby.getTags()));
    lobby.setVisibility(getNewOrDefault(lobbyRequestDTO.visibility(), lobby.getVisibility()));

    return lobbyMapper.toDTO(lobbyRepository.save(lobby));
  }

  private <T> T getNewOrDefault(T newValue, T currentValue) {
    return newValue != null ? newValue : currentValue;
  }

  @Transactional
  public void deleteLobby(Long id, HttpServletRequest request) {
    Lobby lobby = lobbyRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Lobby com o id: " + id + " não encontrado."));

    String token = tokenService.recoverToken(request);
    if (token == null) {
      throw new IllegalArgumentException("Token de sessão não fornecido.");
    }

    User sectionUser = userRepository.findById(tokenService.getTokenId(token))
        .orElseThrow(() -> new EntityNotFoundException("Token de sessão invalido."));

    if (!lobby.getUserPrime().equals(sectionUser)) {
      throw new PermissionDeniedException("Não autorizado a excluir um Lobby que voçe não seja o User Prime.");
    }

    lobby.setStatus(StatusEnum.DELETED);
    lobbyRepository.save(lobby);

  }
}
