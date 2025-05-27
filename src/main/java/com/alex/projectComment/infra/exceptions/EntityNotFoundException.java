package com.alex.projectComment.infra.exceptions;

public class EntityNotFoundException extends RuntimeException {

  public EntityNotFoundException(String msg) {
    super(msg);
  }

}