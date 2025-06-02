package com.alex.projectComment.Lobby.services;

import com.alex.projectComment.Lobby.dtos.LobbyDTO;
import com.alex.projectComment.Lobby.dtos.LobbyRequestDTO;
import com.alex.projectComment.Lobby.dtos.TagDTO;
import com.alex.projectComment.Lobby.entities.Lobby;
import com.alex.projectComment.Lobby.mappers.LobbyMapper;
import com.alex.projectComment.Lobby.mappers.TagMapper;
import com.alex.projectComment.Lobby.repositories.LobbyRepository;
import com.alex.projectComment.User.entities.User;
import com.alex.projectComment.User.repositories.UserRepository;
import com.alex.projectComment.infra.exceptions.AlreadyInUseException;
import com.alex.projectComment.infra.exceptions.EntityNotFoundException;
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

  @Transactional(readOnly = true)
  public Page<LobbyDTO> findAll(Pageable pageable) {
    Page<Lobby> result = lobbyRepository.findAll(pageable);
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

    if (lobbyRepository.existsByNameLikeIgnoreCase(lobbyRequestDTO.name())) {
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

    lobby.setDomains(lobbyRequestDTO.domains());
    lobby.setName(lobbyRequestDTO.name());
    lobby.setTags(tagMapper.listDTOToListEntity(tags));
    lobby.setVisibility(lobbyRequestDTO.visibility());
    lobby.setCreationDate(LocalDateTime.now());

    lobby.setCreator(sectionUser);
    lobby.setUserPrime(sectionUser);
    lobby.setUsersAdmin(admins);

    lobby = lobbyRepository.save(lobby);
    return lobbyMapper.toDTO(lobby);


  }
}
