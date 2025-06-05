package com.alex.projectComment.Lobby.repositories;

import com.alex.projectComment.Lobby.entities.Lobby;
import com.alex.projectComment.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LobbyRepository extends JpaRepository<Lobby, Long> {
  boolean existsByNameLikeIgnoreCase(String name);

  Page<Lobby> findAllByStatus(StatusEnum status, org.springframework.data.domain.Pageable pageable);

  @Query("select l from Lobby l where upper(l.name) = upper(:name) and l.status = :status")
  Optional<Lobby> findByNameAndStatusIgnoreCase(String name, StatusEnum status);

  @Query("select (count(l) > 0) from Lobby l where upper(l.name) = upper(:name) and l.status = :status")
  boolean existsByNameAndStatusIgnoreCase(String name, StatusEnum status);
}
