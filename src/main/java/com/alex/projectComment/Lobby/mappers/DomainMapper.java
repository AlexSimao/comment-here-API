package com.alex.projectComment.Lobby.mappers;

import com.alex.projectComment.Lobby.dtos.DomainDTO;
import com.alex.projectComment.Lobby.entities.Domain;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DomainMapper {

  DomainDTO toDTO(Domain entity);

  Domain toEntity(DomainDTO dto);

  List<DomainDTO> listEntityToListDTO(List<Domain> listTags);

  List<Domain> listDTOToListEntity(List<DomainDTO> listTags);

}
