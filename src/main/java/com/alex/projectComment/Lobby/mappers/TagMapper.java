package com.alex.projectComment.Lobby.mappers;

import com.alex.projectComment.Lobby.dtos.TagDTO;
import com.alex.projectComment.Lobby.entities.Tag;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TagMapper {

  TagDTO toDTO(Tag entity);

  Tag toEntity(TagDTO dto);

  List<TagDTO> listEntityToListDTO(List<Tag> listTags);

  List<Tag> listDTOToListEntity(List<TagDTO> listTags);

}
