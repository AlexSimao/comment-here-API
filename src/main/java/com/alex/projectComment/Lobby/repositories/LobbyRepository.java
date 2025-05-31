package com.alex.projectComment.Lobby.repositories;

import com.alex.projectComment.Lobby.entities.Lobby;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LobbyRepository extends JpaRepository<Lobby, Long> {
}
