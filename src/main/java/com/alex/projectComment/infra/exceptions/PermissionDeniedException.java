package com.alex.projectComment.infra.exceptions;

public class PermissionDeniedException extends RuntimeException {

  public PermissionDeniedException(String msg) {
    super(msg);
  }

}