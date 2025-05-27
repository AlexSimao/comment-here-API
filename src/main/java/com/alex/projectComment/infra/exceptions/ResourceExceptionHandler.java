package com.alex.projectComment.infra.exceptions;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ResourceExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ResponseExceptionErrorDTO> illegalArgument(IllegalArgumentException e,
                                                                   HttpServletRequest request) {

    ResponseExceptionErrorDTO err = new ResponseExceptionErrorDTO();
    err.setTimestamp(Instant.now());
    err.setStatus(HttpStatus.BAD_REQUEST.value());
    err.setError("Bad Request");
    err.setMessage(e.getMessage());
    err.setPath(request.getRequestURI());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ResponseExceptionErrorDTO> entityNotFound(EntityNotFoundException e,
                                                                  HttpServletRequest request) {

    ResponseExceptionErrorDTO err = new ResponseExceptionErrorDTO();
    err.setTimestamp(Instant.now());
    err.setStatus(HttpStatus.NOT_FOUND.value());
    err.setError("Not Found");
    err.setMessage(e.getMessage());
    err.setPath(request.getRequestURI());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
  }

  @ExceptionHandler(AlreadyInUseException.class)
  public ResponseEntity<ResponseExceptionErrorDTO> alreadyInUse(AlreadyInUseException e,
                                                                HttpServletRequest request) {

    ResponseExceptionErrorDTO err = new ResponseExceptionErrorDTO();
    err.setTimestamp(Instant.now());
    err.setStatus(HttpStatus.OK.value());
    err.setError("Already In Use");
    err.setMessage(e.getMessage());
    err.setPath(request.getRequestURI());

    return ResponseEntity.status(HttpStatus.OK).body(err);
  }

  @ExceptionHandler(PermissionDeniedException.class)
  public ResponseEntity<ResponseExceptionErrorDTO> permissionDenied(PermissionDeniedException e,
                                                                HttpServletRequest request) {

    ResponseExceptionErrorDTO err = new ResponseExceptionErrorDTO();
    err.setTimestamp(Instant.now());
    err.setStatus(HttpStatus.FORBIDDEN.value());
    err.setError("Permission Denied");
    err.setMessage(e.getMessage());
    err.setPath(request.getRequestURI());

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(err);
  }

}