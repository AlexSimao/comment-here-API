package com.alex.projectComment.dtos;

import com.alex.projectComment.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RoleDTO {
  private Long id;
  private String name;

  public RoleDTO(Role entity) {
    BeanUtils.copyProperties(entity, this);
  }

  public Role toEntity() {
    return new Role(this.getId(), this.getName());
  }
}