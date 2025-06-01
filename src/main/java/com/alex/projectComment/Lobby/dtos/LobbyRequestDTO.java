package com.alex.projectComment.Lobby.dtos;

import com.alex.projectComment.Lobby.enums.VisibilityEnum;

import java.util.List;

public record LobbyRequestDTO(String name,
                              List<String> tags,
                              List<String> domains,
                              VisibilityEnum visibility,
                              List<Long> usersAdminIds) {

}
