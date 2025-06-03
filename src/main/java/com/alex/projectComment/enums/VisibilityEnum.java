package com.alex.projectComment.enums;

import lombok.Getter;

@Getter
public enum VisibilityEnum {
  PUBLIC(0),
  PRIVATE(1);

  private final int index;

  VisibilityEnum(int index) {
    this.index = index;
  }

}
