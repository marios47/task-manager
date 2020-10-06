package org.apirest.taskmanager.controller;

import io.swagger.annotations.ApiParam;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.apirest.taskmanager.controller.dto.TaskRequest;
import org.apirest.taskmanager.controller.dto.TaskResponse;
import org.apirest.taskmanager.service.CommandService;
import org.apirest.taskmanager.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TaskController implements TaskManagerApi {

  private final QueryService queryService;
  private final CommandService commandService;

  @GetMapping()
  public ResponseEntity<List<TaskResponse>> findAll() {
    return ResponseEntity.ok(queryService.getAll());
  }

  @GetMapping(params = {"page", "size"})
  public ResponseEntity<Page<TaskResponse>> findPage(
      @ApiParam(value = "Number of the page shown") @RequestParam(value = "page",
          defaultValue = DEFAULT_PAGE_NUMBER, required = false) Integer page,
      @ApiParam(value = "Number of records shown in a single page") @RequestParam(value = "size",
          defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer size) {

    return ResponseEntity.ok(queryService.getAll(page, size));
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<TaskResponse> findById(@PathVariable("id") Long id) {
    return ResponseEntity.ok(queryService.getById(id));
  }

  @GetMapping(value = "/name/{name}")
  public ResponseEntity<TaskResponse> findByName(@PathVariable("name") String name) {
    return ResponseEntity.ok(queryService.getByName(name));
  }

  @GetMapping(value = "/finished")
  public ResponseEntity<List<TaskResponse>> findFinished() {
    return ResponseEntity.ok(queryService.getFinished());
  }

  @GetMapping(value = "/unfinished")
  public ResponseEntity<List<TaskResponse>> findUnfinished() {
    return ResponseEntity.ok(queryService.getUnfinished());
  }

  @PostMapping
  public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest taskRequest) {
    return ResponseEntity.ok(commandService.create(taskRequest));
  }

  @PutMapping(value = "/{id}")
  public ResponseEntity<TaskResponse> update(@PathVariable("id") Long id, @Valid @RequestBody TaskRequest taskRequest) {
    return ResponseEntity.ok(commandService.update(id, taskRequest));
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
    commandService.delete(id);
    return ResponseEntity.accepted().build();
  }

  @GetMapping(value = "/finish/{id}")
  public ResponseEntity<Void> finish(Long id) {
    commandService.finish(id);
    return ResponseEntity.accepted().build();
  }
}
