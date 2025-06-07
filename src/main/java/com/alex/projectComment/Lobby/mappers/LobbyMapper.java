package com.alex.projectComment.Lobby.mappers;

import com.alex.projectComment.Lobby.dtos.LobbyDTO;
import com.alex.projectComment.Lobby.entities.Lobby;
import com.alex.projectComment.User.mappers.UserMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface LobbyMapper {

  LobbyDTO toDTO(Lobby entity);

  Lobby toEntity(LobbyDTO dto);

}
