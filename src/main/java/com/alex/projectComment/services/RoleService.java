package com.alex.projectComment.services;

import com.alex.projectComment.dtos.RoleDTO;
import com.alex.projectComment.entities.Role;
import com.alex.projectComment.infra.exceptions.EntityNotFoundException;
import com.alex.projectComment.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class RoleService {
  @Autowired
  private RoleRepository roleRepository;

  @Transactional(readOnly = true)
  public Page<RoleDTO> findAll(Pageable pageable) {
    Page<Role> result = roleRepository.findAll(pageable);
    return result.map(RoleDTO::new);
  }

  @Transactional(readOnly = true)
  public RoleDTO findById(Long id) {
    Role result = roleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Role com id: " + id + " não existe."));
    return new RoleDTO(result);
  }

  @Transactional(readOnly = true)
  public RoleDTO findByName(String name) {
    Role result = roleRepository.findByNameLikeIgnoreCase(name).orElseThrow(() -> new EntityNotFoundException("Role com nome: " + name + " não existe."));
    return new RoleDTO(result);
  }

  @Transactional
  public void createRole(@RequestBody RoleDTO roleDTO) {
    if (roleDTO.getName().isBlank()) {
      throw new IllegalArgumentException("Nome da Role não pode ser vazio ou 'null': " + roleDTO.getName());
    }

    Role newRole = new Role();
    newRole.setName(roleDTO.getName());

    roleRepository.save(newRole);

  }
}
