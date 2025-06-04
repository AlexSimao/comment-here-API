package com.alex.projectComment.Lobby.dtos;

import com.alex.projectComment.enums.VisibilityEnum;

import java.util.List;

public record LobbyUpdateRequestDTO(
    String name,
    List<String> tags,
    List<String> domains,
    VisibilityEnum visibility
) {

}
