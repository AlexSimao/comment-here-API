package com.alex.projectComment.dtos;

import com.alex.projectComment.entities.Role;
import com.alex.projectComment.entities.User;
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
