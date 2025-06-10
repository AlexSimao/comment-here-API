package com.alex.projectComment.Comment.dtos;

import com.alex.projectComment.User.dtos.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LikesDTO {
  private Long id;

  private UserDTO user;

  private CommentDTO comment;

  private boolean isLiked;
  private boolean isDisliked;
}
