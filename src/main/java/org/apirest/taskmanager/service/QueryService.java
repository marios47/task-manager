package org.apirest.taskmanager.service;

import java.util.List;
import org.apirest.taskmanager.controller.dto.TaskResponse;
import org.springframework.data.domain.Page;

public interface QueryService {

  List<TaskResponse> getAll();

  Page<TaskResponse> getAll(Integer page, Integer size);

  TaskResponse getById(Long id);

  TaskResponse getByName(String name);

  List<TaskResponse> getFinished();

  List<TaskResponse> getUnfinished();

}
