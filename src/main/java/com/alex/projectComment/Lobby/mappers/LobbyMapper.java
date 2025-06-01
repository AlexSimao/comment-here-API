package com.alex.projectComment.Lobby.mappers;

import com.alex.projectComment.Lobby.dtos.LobbyDTO;
import com.alex.projectComment.Lobby.entities.Lobby;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LobbyMapper {

  LobbyDTO toDTO(Lobby entity);

  Lobby toEntity(LobbyDTO dto);

}
