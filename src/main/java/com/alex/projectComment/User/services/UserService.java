package com.alex.projectComment.User.services;

import com.alex.projectComment.User.auth.AuthLoginResponseDTO;
import com.alex.projectComment.User.dtos.UserDTO;
import com.alex.projectComment.User.dtos.UserUpdateRequestDTO;
import com.alex.projectComment.User.entities.User;
import com.alex.projectComment.infra.exceptions.AlreadyInUseException;
import com.alex.projectComment.infra.exceptions.EntityNotFoundException;
import com.alex.projectComment.infra.exceptions.PermissionDeniedException;
import com.alex.projectComment.infra.security.TokenService;
import com.alex.projectComment.User.repositories.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private TokenService tokenService;

  @Transactional(readOnly = true)
  public Page<UserDTO> findAll(Pageable pageable) {
    Page<User> result = userRepository.findAll(pageable);
    return result.map(UserDTO::new);
  }

  @Transactional(readOnly = true)
  public UserDTO findById(Long id) {
    User result = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Usuário com id: " + id + " não encontrado"));
    return new UserDTO(result);
  }

  @Transactional
  public AuthLoginResponseDTO updateUser(Long id, UserUpdateRequestDTO userUpdateRequestDTO, HttpServletRequest request) {
    if (userUpdateRequestDTO.getUsername() == null || userUpdateRequestDTO.getUsername().isBlank()) {
      throw new IllegalArgumentException("Username não pode ser vazio ou 'null': " + userUpdateRequestDTO.getUsername());
    }

    if (userRepository.existsByEmailLikeIgnoreCase(userUpdateRequestDTO.getEmail())) {
      throw new AlreadyInUseException("Este Email já esta em uso: " + userUpdateRequestDTO.getEmail());
    }

    if (userRepository.existsByUsernameLikeIgnoreCase(userUpdateRequestDTO.getUsername())) {
      throw new AlreadyInUseException("Este nome de Usuário já esta em uso: " + userUpdateRequestDTO.getUsername());
    }


    User entity = userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Usuário com id: " + id + " não encontrado."));

    String token = tokenService.recoverToken(request);
    if (!tokenService.validateToken(token).equals(entity.getUsername())) {
      throw new PermissionDeniedException("Não autorizado a alterar um usuário que não seja o seu próprio.");
    }

    entity.setFirstName(getNewOrDefault(userUpdateRequestDTO.getFirstName(), entity.getFirstName()));
    entity.setLastName(getNewOrDefault(userUpdateRequestDTO.getLastName(), entity.getLastName()));
    entity.setUsername(getNewOrDefault(userUpdateRequestDTO.getUsername(), entity.getUsername()));
    entity.setEmail(getNewOrDefault(userUpdateRequestDTO.getEmail(), entity.getEmail()));
    entity.setPassword(userUpdateRequestDTO.getPassword() != null
        ? passwordEncoder.encode(userUpdateRequestDTO.getPassword())
        : entity.getPassword());

    userRepository.save(entity);

    token = tokenService.generateKeyToken(entity);
    return new AuthLoginResponseDTO(entity.getUsername(), token, "Usuário Atualizado com Sucesso");

//    Atualização de usuário gera um novo token. Necessário fazer logout(Atualizar token de sessão) para a conclusão da atualização de Usuário.
  }

  //  Método auxiliar para retornar o novo valor se não for nulo, caso contrário retorna o valor atual.
  private <T> T getNewOrDefault(T newValue, T currentValue) {
    return newValue != null ? newValue : currentValue;
  }

  @Transactional
  public void deleteUser(Long id, HttpServletRequest request) {
    User entity = userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Usuário com id: " + id + " não encontrado."));

    String token = tokenService.recoverToken(request);
    if (!tokenService.validateToken(token).equals(entity.getUsername())) {
      throw new PermissionDeniedException("Não autorizado a deletar um usuário que não seja o seu próprio.");
    }

    userRepository.delete(entity);
  }


}
