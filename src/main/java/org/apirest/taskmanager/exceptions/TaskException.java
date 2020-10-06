package org.apirest.taskmanager.exceptions;

import javax.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TaskException extends ResponseStatusException {

  private TaskException(HttpStatus status, String reason, Throwable err) {
    super(status, reason, err);
  }

  public static TaskException idNotFound(Long id) {
    String reason = "Task with id '" + id + "' not found";
    log.error(reason);
    return new TaskException(HttpStatus.NOT_FOUND, reason, new EntityNotFoundException());
  }

  public static TaskException nameNotFound(String name) {
    String reason = "Task with name '" + name + "' not found";
    log.error(reason);
    return new TaskException(HttpStatus.NOT_FOUND, reason, new EntityNotFoundException());
  }

}
