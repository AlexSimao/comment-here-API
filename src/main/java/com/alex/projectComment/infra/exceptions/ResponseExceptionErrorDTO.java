package com.alex.projectComment.infra.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
public class ResponseExceptionErrorDTO {
  private Instant timestamp;
  private Integer status;
  private String error;
  private String message;
  private String path;

  public ResponseExceptionErrorDTO() {

  }

}
