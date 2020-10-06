package org.apirest.taskmanager.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apirest.taskmanager.controller.dto.TaskResponse;
import org.apirest.taskmanager.converter.TaskConverter;
import org.apirest.taskmanager.exceptions.TaskException;
import org.apirest.taskmanager.repository.TaskRepository;
import org.apirest.taskmanager.repository.entities.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TaskQueryService implements QueryService {

  private final TaskRepository taskRepository;
  private final TaskConverter taskConverter;

  @Override
  public List<TaskResponse> getAll() {
    List<Task> tasks = taskRepository.findAll();
    log.info("Obtained " + tasks.size() + " tasks");
    return tasks.stream().map(taskConverter::entityToResponse).collect(Collectors.toList());
  }

  @Override
  public Page<TaskResponse> getAll(Integer page, Integer size) {
    Page<Task> taskPage = taskRepository.findAll(PageRequest.of(page, size));
    log.info("Obtained " + taskPage.getNumberOfElements() + " results in page " + taskPage.getNumber()
        + " from a total of " + taskPage.getTotalElements());
    return taskConverter.pagedEntityToPagedResponse(taskPage);
  }

  @Override
  public TaskResponse getById(Long id) {
    Task task = taskRepository.findById(id).orElseThrow(() -> TaskException.idNotFound(id));
    log.info("Obtained task " + task);
    return taskConverter.entityToResponse(task);
  }

  @Override
  public TaskResponse getByName(String name) {
    Task task = taskRepository.findByName(name).orElseThrow(() -> TaskException.nameNotFound(name));
    log.info("Obtained task " + task);
    return taskConverter.entityToResponse(task);
  }

  @Override
  public List<TaskResponse> getFinished() {
    List<Task> tasks = taskRepository.findByFinished(true);
    log.info("Obtained " + tasks.size() + " finished tasks");
    return tasks.stream().map(taskConverter::entityToResponse).collect(Collectors.toList());
  }

  @Override
  public List<TaskResponse> getUnfinished() {
    List<Task> tasks = taskRepository.findByFinished(false);
    log.info("Obtained " + tasks.size() + " unfinished tasks");
    return tasks.stream().map(taskConverter::entityToResponse).collect(Collectors.toList());
  }
}
