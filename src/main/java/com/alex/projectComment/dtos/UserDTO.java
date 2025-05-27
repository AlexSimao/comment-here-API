package com.alex.projectComment.dtos;

import com.alex.projectComment.entities.User;
import com.alex.projectComment.repositories.UserRepository;
import com.alex.projectComment.services.UserService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

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
