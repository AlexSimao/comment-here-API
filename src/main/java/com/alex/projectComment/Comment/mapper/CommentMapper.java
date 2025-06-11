package com.alex.projectComment.Comment.mapper;

import com.alex.projectComment.Comment.dtos.CommentDTO;
import com.alex.projectComment.Comment.dtos.CommentMinDTO;
import com.alex.projectComment.Comment.entities.Comment;
import com.alex.projectComment.Lobby.mappers.DomainMapper;
import com.alex.projectComment.Section.mapper.SectionMapper;
import com.alex.projectComment.User.mappers.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, SectionMapper.class, DomainMapper.class, LikesMapper.class})
public interface CommentMapper {

  @Mapping(target = "commentsResponses", ignore = true)
  Comment toEntity(CommentDTO commentDTO);

  @Mapping(target = "createdBy.id", source = "userId")
  @Mapping(target = "createdBy.username", source = "userName")
  @Mapping(target = "likes", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "commentsResponses", ignore = true)
  Comment toEntity(CommentMinDTO commentDTO);

  @Mapping(target = "responsesComments", ignore = true)
  CommentDTO toDto(Comment comment);

  @Mapping(target = "responsesComments", ignore = true)
  @Mapping(target = "userId", source = "createdBy.id")
  @Mapping(target = "userName", source = "createdBy.username")
  CommentMinDTO toMinDto(Comment comment);

}