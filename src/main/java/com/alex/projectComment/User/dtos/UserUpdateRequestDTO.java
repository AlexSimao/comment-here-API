package com.alex.projectComment.User.dtos;

import com.alex.projectComment.User.entities.Role;
import com.alex.projectComment.User.entities.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.Set;

@NoArgsConstructor
@Setter
@Getter
public class UserUpdateRequestDTO {


  private String firstName;
  private String lastName;
  private String username;
  private String email;
  private String password;
  private Set<Role> roles;

  public UserUpdateRequestDTO(User entity) {
    BeanUtils.copyProperties(entity, this);
  }

}
