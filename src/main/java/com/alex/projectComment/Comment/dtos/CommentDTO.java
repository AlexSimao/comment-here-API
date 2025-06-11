package com.alex.projectComment.Comment.dtos;

import com.alex.projectComment.Lobby.dtos.DomainDTO;
import com.alex.projectComment.Section.dtos.SectionDTO;
import com.alex.projectComment.User.dtos.UserDTO;
import com.alex.projectComment.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CommentDTO {
  private Long id;
  private String content;

  private UserDTO createdBy;

  private LocalDateTime createdAt;

  private boolean isEdited;

  private List<CommentDTO> responsesComments;

  private SectionDTO section;

  private DomainDTO domain;

  private StatusEnum status;

  private Integer likesCount;
  private Integer dislikesCount;

  private List<LikesDTO> likes;
}
