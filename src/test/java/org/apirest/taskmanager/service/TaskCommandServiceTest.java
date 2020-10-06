package org.apirest.taskmanager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.apirest.taskmanager.controller.dto.TaskRequest;
import org.apirest.taskmanager.controller.dto.TaskResponse;
import org.apirest.taskmanager.converter.TaskConverter;
import org.apirest.taskmanager.exceptions.TaskException;
import org.apirest.taskmanager.repository.TaskRepository;
import org.apirest.taskmanager.repository.entities.Task;
import org.apirest.taskmanager.utils.TestRequestFactory;
import org.apirest.taskmanager.utils.TestTaskFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TaskCommandServiceTest {

  @Mock
  private TaskRepository taskRepository;
  private TaskCommandService taskCommandService;

  private final TaskConverter taskConverter = Mappers.getMapper(TaskConverter.class);
  private final TestTaskFactory taskFactory = new TestTaskFactory();
  private final TestRequestFactory requestFactory = new TestRequestFactory();

  @Before
  public void setUp() {
    this.taskCommandService = new TaskCommandService(taskRepository, taskConverter);
  }

  @Test
  public void whenCreatingNewTask_givenNewTask_ThenDo() {
    // given
    TaskRequest newTask = requestFactory.generate();

    // when
    Task storedTask = taskConverter.requestToEntity(newTask);
    when(taskRepository.save(any(Task.class))).thenReturn(storedTask);

    // then
    taskCommandService.create(newTask);

    assertThat(newTask).isEqualToComparingFieldByField(storedTask);
    verify(taskRepository).save(storedTask);
  }

  @Test
  public void whenUpdatingTask_givenTaskExistsInDatabase_ThenDo() {
    // given
    long taskId = 1;
    TaskRequest updateRequest = requestFactory.generate();

    // when
    Task oldTask = taskFactory.generateWithId(taskId);
    Task updatedTask = updateTaskWithTaskRequest(oldTask, updateRequest);

    when(taskRepository.findById(any(Long.class))).thenReturn(Optional.of(oldTask));
    when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

    // then
    TaskResponse createdTask = taskCommandService.update(taskId, updateRequest);

    assertThat(updateRequest).isEqualToComparingFieldByField(createdTask);
    verify(taskRepository).findById(taskId);
    verify(taskRepository).save(updatedTask);
  }

  private Task updateTaskWithTaskRequest(Task task, TaskRequest request) {
    return Task.builder()
        .id(task.getId())
        .name(request.getName())
        .description(request.getDescription())
        .finished(task.isFinished())
        .build();
  }

  @Test
  public void whenUpdatingTask_givenTaskNotExistsInDatabase_ThenThrowException() {
    // given
    long taskId = 1;
    TaskRequest updateRequest = requestFactory.generate();

    // when
    Optional<Task> oldTask = Optional.empty();
    TaskException taskEx = TaskException.idNotFound(taskId);
    when(taskRepository.findById(any(Long.class))).thenReturn(oldTask);

    // then
    Exception ex = Assertions.assertThrows(TaskException.class, () -> taskCommandService.update(taskId, updateRequest));

    assertThat(ex).isEqualToComparingFieldByField(taskEx);
    verify(taskRepository).findById(taskId);
    verify(taskRepository, Mockito.times(0)).save(any());
  }

  @Test
  public void whenDeletingTask_givenTaskExistsInDatabase_ThenDo() {
    // given
    long taskId = 1;
    // when
    taskCommandService.delete(taskId);
    // then
    verify(taskRepository).deleteById(taskId);
  }

  @Test
  public void whenFinishTask_givenTaskExistsInDatabase_ThenDo() {
    // given
    long taskId = 1;
    // when
    Task oldTask = taskFactory.generateWithId(taskId);
    when(taskRepository.findById(any(Long.class))).thenReturn(Optional.of(oldTask));
    taskCommandService.finish(taskId);
    // then
    verify(taskRepository).save(oldTask);
  }

  @Test
  public void whenDeletingTask_givenTaskNotExistsInDatabase_ThenThrowException() {
    // given
    long taskId = 1;

    // when
    Optional<Task> task = Optional.empty();
    when(taskRepository.findById(any(Long.class))).thenReturn(task);

    // then
    Exception ex = Assertions.assertThrows(TaskException.class, () -> taskCommandService.finish(taskId));

    assertThat(ex).isEqualToComparingFieldByField(TaskException.idNotFound(taskId));
    verify(taskRepository).findById(taskId);
    verify(taskRepository, Mockito.times(0)).save(any());
  }

}
