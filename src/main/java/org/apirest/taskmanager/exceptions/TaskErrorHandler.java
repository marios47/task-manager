package org.apirest.taskmanager.exceptions;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class TaskErrorHandler {

  @ExceptionHandler(TaskException.class)
  public ResponseEntity handle(TaskException taskException) {
    return new ResponseEntity<>(taskException.getReason(), taskException.getStatus());
  }

  @ExceptionHandler(NumberFormatException.class)
  public ResponseEntity handle(NumberFormatException ex) {
    String wrongValue = "";
    if (ex.getMessage() != null && ex.getMessage().contains("For input string")) {
      wrongValue = "'" + ex.getMessage().substring(19, ex.getMessage().length() - 1) + "' ";
    }
    String message = "The value " + wrongValue + "provided is not correct. Please provide a number";
    return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(EmptyResultDataAccessException.class)
  public ResponseEntity handle(EmptyResultDataAccessException ex) {
    return new ResponseEntity<>("Task not found", HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity handle(MethodArgumentNotValidException ex) {
    String errors = getErrorsFromMethodArgumentNotValidException(ex);
    return new ResponseEntity(errors, HttpStatus.BAD_REQUEST);
  }

  private String getErrorsFromMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
    StringBuilder errors = new StringBuilder();
    AtomicInteger counter = new AtomicInteger();
    ex.getBindingResult().getAllErrors().stream().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      if (counter.get() > 0) {
        errors.append(". ");
      }
      errors.append("Error on field '").append(fieldName).append("': ").append(errorMessage);
      counter.getAndIncrement();
    });
    return errors.toString();
  }
}