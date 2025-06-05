package com.alex.projectComment.Lobby.repositories;

import com.alex.projectComment.Lobby.entities.Domain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DomainRepository extends JpaRepository<Domain, Long> {
  boolean existsByNameIgnoreCase(String name);

  Optional<Domain> findByNameLikeIgnoreCase(String name);
}
