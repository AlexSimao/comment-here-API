package com.alex.projectComment.Section.dtos;

import com.alex.projectComment.enums.StatusEnum;

public record SectionUpdateRequestDTO(String name, StatusEnum status) {
}
