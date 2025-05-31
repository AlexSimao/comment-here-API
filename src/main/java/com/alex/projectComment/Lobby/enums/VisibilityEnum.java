package com.alex.projectComment.Lobby.enums;

public enum VisibilityEnum {
  PUBLIC(0),
  PRIVATE(1);

  private final int index;

  VisibilityEnum(int index) {
    this.index = index;
  }

  public int getIndex() {
    return index;
  }

  public static boolean exists(String visibility) {
    try {
      valueOf(visibility);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

}
