package com.alex.projectComment.Lobby.services;

import com.alex.projectComment.Lobby.dtos.DomainDTO;
import com.alex.projectComment.Lobby.entities.Domain;
import com.alex.projectComment.Lobby.mappers.DomainMapper;
import com.alex.projectComment.Lobby.repositories.DomainRepository;
import com.alex.projectComment.infra.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DomainService {
  @Autowired
  private DomainRepository domainRepository;
  @Autowired
  private DomainMapper domainMapper;

  @Transactional(readOnly = true)
  public Page<DomainDTO> findAll(Pageable pageable) {
    Page<Domain> result = domainRepository.findAll(pageable);
    return result.map(domainMapper::toDTO);
  }

  @Transactional(readOnly = true)
  public DomainDTO findByName(String name) {
    Domain result = domainRepository.findByNameLikeIgnoreCase(name)
        .orElseThrow(() -> new EntityNotFoundException("Não existe um domínio com esse nome: " + name));
    return domainMapper.toDTO(result);
  }

  @Transactional(readOnly = true)
  public boolean existsByName(String name) {
    return domainRepository.existsByNameIgnoreCase(name);
  }

  @Transactional
  public DomainDTO createDomain(String domainName) {
    if (domainRepository.existsByNameIgnoreCase(domainName)) {
      throw new IllegalArgumentException("Domínio com o nome de: " + domainName + " já existe.");
    }

    Domain newDomain = new Domain();
    newDomain.setName(domainName);

    newDomain = domainRepository.save(newDomain);

    return domainMapper.toDTO(newDomain);
  }

}
