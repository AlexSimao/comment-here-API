package com.alex.projectComment.infra.exceptions;

public class AlreadyInUseException extends RuntimeException {
  public AlreadyInUseException(String msg) {
    super(msg);
  }

}