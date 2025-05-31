package com.alex.projectComment.Lobby.dtos;

import com.alex.projectComment.Lobby.entities.Lobby;
import com.alex.projectComment.Lobby.enums.VisibilityEnum;
import com.alex.projectComment.User.dtos.UserDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class LobbyDTO {
  private Long id;
  private String name;
  private LocalDateTime creationDate;
  private List<String> tags;
  private List<String> domains;

  private VisibilityEnum visibility;

  private UserDTO creator;
  private UserDTO userPrime;

  private List<UserDTO> usersAdmin;

  public LobbyDTO(Lobby entity) {
    BeanUtils.copyProperties(entity, this);
  }

}
