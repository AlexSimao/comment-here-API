package com.alex.projectComment.User.repositories;

import com.alex.projectComment.User.entities.User;
import com.alex.projectComment.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsernameLikeIgnoreCase(String username);

  @Query("select u from User u where upper(u.username) = upper(:username) and u.status = :status")
  Optional<User> findByUsernameAndStatusIgnoreCase(String username, StatusEnum status);

  Page<User> findAllByStatus(StatusEnum status, org.springframework.data.domain.Pageable pageable);

  boolean existsByEmailLikeIgnoreCase(String email);

  boolean existsByUsernameLikeIgnoreCase(String username);

  @Query("select (count(u) > 0) from User u where upper(u.username) = upper(:username) and u.status = :status")
  boolean existsByUsernameAndStatusIgnoreCase(@Param("username") String username,
                                              @Param("status") StatusEnum status);

  @Query("select (count(u) > 0) from User u where upper(u.email) = upper(:email) and u.status = :status")
  boolean existsByEmailAndStatusIgnoreCase(@Param("email") String email,
                                           @Param("status") StatusEnum status);


}
