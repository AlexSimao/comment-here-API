package com.alex.projectComment.Section.dtos;

import com.alex.projectComment.Lobby.dtos.LobbyDTO;
import com.alex.projectComment.User.dtos.UserDTO;
import com.alex.projectComment.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SectionDTO {
  private Long id;
  private String name;
  private LocalDateTime createdAt;
  private UserDTO createdBy;
  private LobbyDTO lobby;
  private StatusEnum status;
}
