package com.alex.projectComment.User.controllers;

import com.alex.projectComment.User.auth.AuthLoginResponseDTO;
import com.alex.projectComment.User.dtos.UserDTO;
import com.alex.projectComment.User.dtos.UserUpdateRequestDTO;
import com.alex.projectComment.User.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

  @Autowired
  private UserService userService;

  @GetMapping
  public ResponseEntity<Page<UserDTO>> findAll(Pageable pageable) {
    Page<UserDTO> result = userService.findAll(pageable);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
    UserDTO result = userService.findById(id);
    return ResponseEntity.ok(result);
  }

  @PutMapping("/{id}")
  public ResponseEntity<AuthLoginResponseDTO> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequestDTO userUpdateRequestDTO, HttpServletRequest request) {
    AuthLoginResponseDTO result = userService.updateUser(id, userUpdateRequestDTO, request);
    return ResponseEntity.ok(result);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id, HttpServletRequest request) {
    userService.deleteUser(id, request);
    return ResponseEntity.ok().build();
  }

}
