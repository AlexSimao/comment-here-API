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

  Comment toEntity(CommentDTO commentDTO);

  @Mapping(target = "user.id", source = "userId")
  @Mapping(target = "user.username", source = "userName")
  @Mapping(target = "likes", ignore = true)
  @Mapping(target = "user", ignore = true)
  Comment toEntity(CommentMinDTO commentDTO);

  CommentDTO toDto(Comment comment);

  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "userName", source = "user.username")
  CommentMinDTO toMinDto(Comment comment);

}