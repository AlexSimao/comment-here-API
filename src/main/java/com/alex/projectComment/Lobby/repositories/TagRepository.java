package com.alex.projectComment.Lobby.repositories;

import com.alex.projectComment.Lobby.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
  boolean existsByNameIgnoreCase(String name);

  Optional<Tag> findByNameLikeIgnoreCase(String name);

}