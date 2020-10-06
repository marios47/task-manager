package org.apirest.taskmanager.converter;

import java.util.stream.Collectors;
import org.apirest.taskmanager.controller.dto.TaskRequest;
import org.apirest.taskmanager.controller.dto.TaskResponse;
import org.apirest.taskmanager.repository.entities.Task;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@Mapper(componentModel = "spring")
public abstract class TaskConverter {

  public abstract Task requestToEntity(TaskRequest request);

  public abstract TaskResponse entityToResponse(Task entity);

  public Page<TaskResponse> pagedEntityToPagedResponse(Page<Task> entity) {
    return new PageImpl<>(
        entity.get().map(this::entityToResponse).collect(Collectors.toList()),
        entity.getPageable(),
        entity.getTotalElements());
  }
}
