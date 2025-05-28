package com.alex.projectComment.User.repositories;

import com.alex.projectComment.User.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
  boolean existsByNameLikeIgnoreCase(String name);

  Optional<Role> findByNameLikeIgnoreCase(String name);

}