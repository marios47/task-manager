package org.apirest.taskmanager.service;

import org.apirest.taskmanager.controller.dto.TaskRequest;
import org.apirest.taskmanager.controller.dto.TaskResponse;

public interface CommandService {

  TaskResponse create(TaskRequest taskRequest);

  TaskResponse update(Long taskId, TaskRequest taskRequest);

  void delete(Long id);

  void finish(Long id);
}
