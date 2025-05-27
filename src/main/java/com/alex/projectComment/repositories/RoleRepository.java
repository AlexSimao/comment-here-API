package com.alex.projectComment.repositories;

import com.alex.projectComment.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
  boolean existsByNameLikeIgnoreCase(String name);

  Optional<Role> findByNameLikeIgnoreCase(String name);

}