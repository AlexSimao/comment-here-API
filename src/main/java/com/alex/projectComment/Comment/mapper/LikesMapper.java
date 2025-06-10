package com.alex.projectComment.Comment.mapper;

import com.alex.projectComment.Comment.dtos.LikesDTO;
import com.alex.projectComment.Comment.entities.Likes;
import com.alex.projectComment.User.mappers.UserMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface LikesMapper {

  Likes toEntity(LikesDTO likesDTO);

  LikesDTO toDto(Likes likes);
}