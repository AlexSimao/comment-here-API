package com.alex.projectComment.User.dtos;

import com.alex.projectComment.User.entities.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@NoArgsConstructor
@Setter
@Getter
public class UserDTO {


  private Long id;
  private String firstName;
  private String lastName;
  private String username;
  private String email;

  public UserDTO(User entity) {
    BeanUtils.copyProperties(entity, this);
  }

}
