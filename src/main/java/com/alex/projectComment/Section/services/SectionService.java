package com.alex.projectComment.Section.services;

import com.alex.projectComment.Lobby.entities.Lobby;
import com.alex.projectComment.Lobby.mappers.LobbyMapper;
import com.alex.projectComment.Lobby.services.LobbyService;
import com.alex.projectComment.Section.dtos.SectionDTO;
import com.alex.projectComment.Section.dtos.SectionRequestDTO;
import com.alex.projectComment.Section.dtos.SectionUpdateRequestDTO;
import com.alex.projectComment.Section.entities.Section;
import com.alex.projectComment.Section.mapper.SectionMapper;
import com.alex.projectComment.Section.repositories.SectionRepository;
import com.alex.projectComment.User.entities.User;
import com.alex.projectComment.User.mappers.UserMapper;
import com.alex.projectComment.User.services.UserService;
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

@Service
public class SectionService {
  @Autowired
  private SectionRepository sectionRepository;
  @Autowired
  private SectionMapper sectionMapper;
  @Autowired
  private LobbyService lobbyService;
  @Autowired
  private LobbyMapper lobbyMapper;
  @Autowired
  private TokenService tokenService;
  @Autowired
  private UserService userService;
  @Autowired
  private UserMapper userMapper;

  @Transactional(readOnly = true)
  public Page<SectionDTO> findAll(Pageable pageable) {
    Page<Section> result = sectionRepository.findAllByStatusNot(StatusEnum.DELETED, pageable);
    return result.map(sectionMapper::toDTO);
  }

  @Transactional(readOnly = true)
  public SectionDTO findById(Long id) {
    Section result = sectionRepository.findByIdWithStatusNotDeleted(id)
        .orElseThrow(() -> new EntityNotFoundException("Section com ID " + id + " não encontrada"));

    return sectionMapper.toDTO(result);
  }

  @Transactional(readOnly = true)
  public boolean existsById(Long id, StatusEnum status) {
    return sectionRepository.existsByIdAndStatusNot(id, status);
  }

  @Transactional(readOnly = true)
  public SectionDTO findByName(String name) {
    Section result = sectionRepository.findByNameWithStatusNotDeleted(name)
        .orElseThrow(() -> new EntityNotFoundException("Section com o nome: " + name + " não encontrado."));

    return sectionMapper.toDTO(result);
  }

  @Transactional
  public SectionDTO createSection(Long lobbyId, SectionRequestDTO sectionRequestDTO, HttpServletRequest request) {
    Lobby lobby = lobbyMapper.toEntity(lobbyService.findById(lobbyId));
    User userSection = getUserFromRequest(request);

    if (lobby.getStatus() != StatusEnum.ACTIVE) {
      throw new EntityNotFoundException("Lobby com ID " + lobbyId + " não está ativo.");
    }

    if (!lobby.getUsersAdmin().contains(userSection)) {
      throw new PermissionDeniedException("Usuário não tem permissão para criar seções neste lobby.");
    }

    if (sectionRepository.findByNameWithStatusNotDeleted(sectionRequestDTO.name()).isPresent()) {
      throw new AlreadyInUseException("Section com o nome: " + sectionRequestDTO.name() + " já existe.");
    }

    if (sectionRequestDTO.name() == null || sectionRequestDTO.name().isBlank()) {
      throw new IllegalArgumentException("O nome da seção não pode ser nulo ou vazio.");
    }

    if (sectionRepository.existsByNameAndStatusNot(sectionRequestDTO.name(), StatusEnum.DELETED)) {
      throw new AlreadyInUseException("Já existe uma seção com o nome: " + sectionRequestDTO.name() + " que não está deletada.");
    }

    Section newSection = new Section();
    newSection.setName(sectionRequestDTO.name());
    newSection.setLobby(lobby);
    newSection.setStatus(StatusEnum.ACTIVE);
    newSection.setCreatedAt(LocalDateTime.now());
    newSection.setCreatedBy(userSection);

    return sectionMapper.toDTO(sectionRepository.save(newSection));

  }

  @Transactional
  public SectionDTO updateSection(Long id, SectionUpdateRequestDTO sectionUpdateRequestDTO, HttpServletRequest request) {
    Section section = sectionMapper.toEntity(this.findById(id));
    User userSection = getUserFromRequest(request);

    if (!section.getLobby().getUsersAdmin().contains(userSection)) {
      throw new PermissionDeniedException("Usuário não tem permissão para atualizar seções neste lobby.");
    }

    if (sectionUpdateRequestDTO.name() != null && sectionUpdateRequestDTO.name().isBlank()) {
      throw new IllegalArgumentException("O nome da seção não pode ser vazio.");
    }

    if (sectionRepository.existsByNameAndStatusNot(sectionUpdateRequestDTO.name(), StatusEnum.DELETED)) {
      throw new IllegalArgumentException("Já existe uma seção com o nome: " + sectionUpdateRequestDTO.name() + " que não está deletada.");
    }

    if (sectionUpdateRequestDTO.status() == StatusEnum.DELETED) {
      throw new IllegalArgumentException("Não é permitido atualizar o status para DELETED. Use o método de exclusão.");
    }

    section.setName(getOrDefault(sectionUpdateRequestDTO.name(), section.getName()));
    section.setStatus(getOrDefault(sectionUpdateRequestDTO.status(), section.getStatus()));

    section = sectionRepository.save(section);

    return sectionMapper.toDTO(sectionRepository.save(section));
  }

  @Transactional
  public void deleteSection(Long id, HttpServletRequest request) {
    Section section = sectionMapper.toEntity(this.findById(id));
    User userSection = getUserFromRequest(request);

    if (!section.getLobby().getUsersAdmin().contains(userSection)) {
      throw new PermissionDeniedException("Usuário não tem permissão para deletar seções neste lobby.");
    }

    section.setStatus(StatusEnum.DELETED);
    sectionRepository.save(section);
  }

  private User getUserFromRequest(HttpServletRequest request) {
    String token = tokenService.recoverToken(request);
    return userMapper.toEntity(userService.findById(tokenService.getTokenId(token)));
  }

  private <T> T getOrDefault(T newValue, T defaultValue) {
    return newValue != null ? newValue : defaultValue;
  }
}
