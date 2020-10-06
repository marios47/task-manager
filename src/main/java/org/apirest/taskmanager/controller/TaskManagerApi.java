package org.apirest.taskmanager.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import javax.validation.Valid;
import org.apirest.taskmanager.controller.dto.TaskRequest;
import org.apirest.taskmanager.controller.dto.TaskResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Api(value = "task-manager")
public interface TaskManagerApi {

  String DEFAULT_PAGE_NUMBER = "0";
  String DEFAULT_PAGE_SIZE = "15";

  @ApiOperation(value = "Get a list of Tasks", nickname = "findAll",
      notes = "This operation returns a list of tasks", response = TaskResponse.class, responseContainer = "Page")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Successful operation", response = TaskResponse.class, responseContainer = "Page"),
      @ApiResponse(code = 500, message = "Unexpected error", response = String.class)})
  ResponseEntity<List<TaskResponse>> findAll();

  @ApiOperation(value = "Get a paginated list of Tasks", nickname = "findPage",
      notes = "This operation returns a page of tasks", response = TaskResponse.class, responseContainer = "list")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Successful operation", response = TaskResponse.class, responseContainer = "List"),
      @ApiResponse(code = 500, message = "Unexpected error", response = String.class)})
  ResponseEntity<Page<TaskResponse>> findPage(
      @ApiParam(value = "Number of the page shown") @RequestParam(value = "page",
          defaultValue = DEFAULT_PAGE_NUMBER, required = false) Integer page,
      @ApiParam(value = "Number of records shown in a single page") @RequestParam(value = "size",
          defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer size);

  @ApiOperation(value = "Get a task given its id", nickname = "findById",
      notes = "This operation returns the task when its id is provided", response = TaskResponse.class)
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Successful operation", response = TaskResponse.class),
      @ApiResponse(code = 404, message = "Not found", response = String.class),
      @ApiResponse(code = 500, message = "Unexpected error", response = String.class)})
  ResponseEntity<TaskResponse> findById(@PathVariable("id") Long id);

  @ApiOperation(value = "Get a task given its name", nickname = "findByName",
      notes = "This operation returns the task when its name is provided", response = TaskResponse.class)
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Successful operation", response = TaskResponse.class),
      @ApiResponse(code = 404, message = "Not found", response = String.class),
      @ApiResponse(code = 500, message = "Unexpected error", response = String.class)})
  ResponseEntity<TaskResponse> findByName(@PathVariable("name") String name);

  @ApiOperation(value = "Get finished tasks", nickname = "findFinished",
      notes = "This operation returns the tasks that are finished", response = TaskResponse.class, responseContainer = "List")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Successful operation", response = TaskResponse.class, responseContainer = "List"),
      @ApiResponse(code = 404, message = "Not found", response = String.class),
      @ApiResponse(code = 500, message = "Unexpected error", response = String.class)})
  ResponseEntity<List<TaskResponse>> findFinished();

  @ApiOperation(value = "Get unfinished tasks", nickname = "findUnfinished",
      notes = "This operation returns the tasks that are not finished", response = TaskResponse.class, responseContainer = "List")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Successful operation", response = TaskResponse.class, responseContainer = "List"),
      @ApiResponse(code = 404, message = "Not found", response = String.class),
      @ApiResponse(code = 500, message = "Unexpected error", response = String.class)})
  ResponseEntity<List<TaskResponse>> findUnfinished();

  @ApiOperation(value = "Create a new task", nickname = "create",
      notes = "This operation creates a new the task", response = TaskResponse.class)
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Successful operation", response = TaskResponse.class),
      @ApiResponse(code = 400, message = "Bad request / Invalid data", response = String.class),
      @ApiResponse(code = 500, message = "Unexpected error", response = String.class)})
  ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest taskRequest);

  @ApiOperation(value = "Update a task", nickname = "update",
      notes = "This operation updates a task", response = TaskResponse.class)
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Successful operation", response = TaskResponse.class),
      @ApiResponse(code = 400, message = "Bad request / Invalid data", response = String.class),
      @ApiResponse(code = 500, message = "Unexpected error", response = String.class)})
  ResponseEntity<TaskResponse> update(@PathVariable("id") Long id, @Valid @RequestBody TaskRequest taskRequest);

  @ApiOperation(value = "Delete a task", nickname = "delete",
      notes = "This operation deletes a task", response = String.class)
  @ApiResponses(value = {
      @ApiResponse(code = 202, message = "Accepted", response = String.class),
      @ApiResponse(code = 500, message = "Unexpected error", response = String.class)})
  ResponseEntity<Void> delete(@PathVariable("id") Long id);

  @ApiOperation(value = "Finish task", nickname = "finish",
      notes = "This operation marks a task as finished", response = TaskResponse.class)
  @ApiResponses(value = {
      @ApiResponse(code = 202, message = "Successful operation", response = String.class),
      @ApiResponse(code = 500, message = "Unexpected error", response = String.class)})
  ResponseEntity<Void> finish(@PathVariable("id") Long id);
}
