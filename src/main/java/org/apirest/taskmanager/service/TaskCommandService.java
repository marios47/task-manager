package org.apirest.taskmanager.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apirest.taskmanager.controller.dto.TaskRequest;
import org.apirest.taskmanager.controller.dto.TaskResponse;
import org.apirest.taskmanager.converter.TaskConverter;
import org.apirest.taskmanager.exceptions.TaskException;
import org.apirest.taskmanager.repository.TaskRepository;
import org.apirest.taskmanager.repository.entities.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TaskCommandService implements CommandService {

  private final TaskRepository taskRepository;
  private final TaskConverter taskConverter;

  @Override
  public TaskResponse create(TaskRequest taskRequest) {
    Task newTask = taskRepository.save(taskConverter.requestToEntity(taskRequest));
    log.info("Created new task " + newTask);
    return taskConverter.entityToResponse(newTask);
  }

  @Override
  public TaskResponse update(Long id, TaskRequest taskRequest) {
    Task task = taskRepository.findById(id).orElseThrow(() -> TaskException.idNotFound(id));
    task.setName(taskRequest.getName());
    task.setDescription(taskRequest.getDescription());
    taskRepository.save(task);
    log.info("Updated task " + task);
    return taskConverter.entityToResponse(task);
  }

  @Override
  public void delete(Long id) {
    taskRepository.deleteById(id);
    log.info("Deleted task with id " + id);
  }

  @Override
  public void finish(Long id) {
    Task task = taskRepository.findById(id).orElseThrow(() -> TaskException.idNotFound(id));
    task.setFinished(true);
    taskRepository.save(task);
    log.info("Finished task with id " + id);
  }

}
