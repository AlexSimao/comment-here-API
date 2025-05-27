package com.alex.projectComment.repositories;

import com.alex.projectComment.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsernameLikeIgnoreCase(String username);

  boolean existsByEmailLikeIgnoreCase(String email);

  boolean existsByUsernameLikeIgnoreCase(String username);

}
