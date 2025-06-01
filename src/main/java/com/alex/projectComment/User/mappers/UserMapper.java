package com.alex.projectComment.User.mappers;

import com.alex.projectComment.User.dtos.UserDTO;
import com.alex.projectComment.User.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

  UserDTO toDTO(User entity);

  @Mapping(target = "password", ignore = true)
  @Mapping(target = "roles", ignore = true)
  User toEntity(UserDTO dto);

}
