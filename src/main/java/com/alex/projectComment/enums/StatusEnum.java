package com.alex.projectComment.enums;

import lombok.Getter;

@Getter
public enum StatusEnum {
  INACTIVE(0),
  ACTIVE(1),
  DELETED(2);

  private final int index;

  StatusEnum(int index) {
    this.index = index;
  }
}
