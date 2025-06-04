package com.alex.projectComment.Lobby.controllers;

import com.alex.projectComment.Lobby.dtos.LobbyDTO;
import com.alex.projectComment.Lobby.dtos.LobbyRequestDTO;
import com.alex.projectComment.Lobby.dtos.LobbyUpdateRequestDTO;
import com.alex.projectComment.Lobby.services.LobbyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lobby")
public class LobbyController {

  @Autowired
  private LobbyService lobbyService;

  @GetMapping
  public ResponseEntity<Page<LobbyDTO>> findAll(Pageable pageable) {
    Page<LobbyDTO> result = lobbyService.findAll(pageable);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/{id}")
  public ResponseEntity<LobbyDTO> findById(@PathVariable Long id) {
    LobbyDTO result = lobbyService.findById(id);
    return ResponseEntity.ok(result);
  }

  @PostMapping
  public ResponseEntity<LobbyDTO> create(@RequestBody LobbyRequestDTO lobbyRequestDTO, HttpServletRequest request) {
    LobbyDTO result = lobbyService.createLobby(lobbyRequestDTO, request);
    return ResponseEntity.ok(result);
  }

  @PutMapping("/{id}")
  public ResponseEntity<LobbyDTO> update(@PathVariable Long id, @RequestBody LobbyUpdateRequestDTO lobbyRequestDTO, HttpServletRequest request) {
    LobbyDTO result = lobbyService.updateLobby(id, lobbyRequestDTO, request);
    return ResponseEntity.ok(result);
  }
}
