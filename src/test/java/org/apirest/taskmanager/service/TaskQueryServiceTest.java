package org.apirest.taskmanager.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.apirest.taskmanager.controller.dto.TaskResponse;
import org.apirest.taskmanager.converter.TaskConverter;
import org.apirest.taskmanager.exceptions.TaskException;
import org.apirest.taskmanager.repository.TaskRepository;
import org.apirest.taskmanager.repository.entities.Task;
import org.apirest.taskmanager.utils.TestTaskFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RunWith(MockitoJUnitRunner.class)
public class TaskQueryServiceTest {

  @Mock
  private TaskRepository taskRepository;
  private TaskQueryService taskQueryService;

  private final TaskConverter taskConverter = Mappers.getMapper(TaskConverter.class);
  private final TestTaskFactory taskFactory = new TestTaskFactory();

  @Before
  public void setUp() {
    this.taskQueryService = new TaskQueryService(taskRepository, taskConverter);
  }

  @Test
  public void whenConsultingAll_givenSomeTasksExistOnRepository_thenRetrieveSuccessfully() {
    // given
    List<Task> storedTasks = taskFactory.generateList();
    // when
    when(taskRepository.findAll()).thenReturn(storedTasks);
    // then
    List<TaskResponse> tasks = taskQueryService.getAll();

    assertResponseListMatchesRepositoryList(tasks, storedTasks);
  }

  private void assertResponseListMatchesRepositoryList(List<TaskResponse> responseList, List<Task> repositoryList) {
    assertThat(responseList.size()).isEqualTo(repositoryList.size());
    IntStream.range(0, responseList.size() - 1).forEach(
        i -> assertThat(responseList.get(i)).isEqualToComparingFieldByField(repositoryList.get(i))
    );
  }

  @Test
  public void whenConsultingTaskPage_givenSomeTasksExistOnRepository_thenRetrieveSuccessfully() {
    // given
    int page = 2;
    int size = 10;

    // when
    Page<Task> pagedStoredTasks = taskFactory.generatePage(2, 10);
    when(taskRepository.findAll(any(Pageable.class))).thenReturn(pagedStoredTasks);

    // then
    Page<TaskResponse> pagedTasks = taskQueryService.getAll(page, size);

    assertResponsePageMatchesRepositoryPage(pagedTasks, pagedStoredTasks);
    verify(taskRepository).findAll(PageRequest.of(page, size));
  }

  private void assertResponsePageMatchesRepositoryPage(Page<TaskResponse> responsePage, Page<Task> repositoryPage) {
    assertResponseListMatchesRepositoryList(responsePage.getContent(), repositoryPage.getContent());
    assertThat(responsePage.getPageable()).usingRecursiveComparison().isEqualTo(repositoryPage.getPageable());
    assertThat(responsePage.getTotalElements()).isEqualToComparingFieldByField(repositoryPage.getTotalElements());
  }

  @Test
  public void whenConsultingById_givenTasksWithThatIdOnRepository_thenRetrieveSuccessfully() {
    // given
    Long id = 1L;
    // when
    Optional<Task> storedTask = Optional.of(taskFactory.generateWithId(id));
    when(taskRepository.findById(any(Long.class))).thenReturn(storedTask);
    // then
    TaskResponse task = taskQueryService.getById(id);

    assertThat(storedTask).isPresent();
    assertThat(task).isEqualToComparingFieldByField(storedTask.get());
    verify(taskRepository).findById(id);
  }

  @Test(expected = TaskException.class)
  public void whenConsultingById_givenNoTasksWithThatIdOnRepository_thenThrowException() {
    // given
    Long id = 1L;
    // when
    Optional<Task> storedTask = Optional.empty();
    when(taskRepository.findById(any(Long.class))).thenReturn(storedTask);
    // then
    taskQueryService.getById(id);
  }


  @Test
  public void whenConsultingByName_givenSomeTasksOnRepository_thenRetrieveFromRepository() {
    // given
    String name = "A name";
    // when
    Optional<Task> storedTask = Optional.of(taskFactory.generateWithName(name));
    when(taskRepository.findByName(any(String.class))).thenReturn(storedTask);
    // then
    TaskResponse task = taskQueryService.getByName(name);

    assertThat(storedTask).isPresent();
    assertThat(task).isEqualToComparingFieldByField(storedTask.get());
    verify(taskRepository).findByName(name);
  }

  @Test(expected = TaskException.class)
  public void whenConsultingByName_givenNoTasksWithThatNameOnRepository_thenThrowException() {
    // given
    String name = "A name";
    // when
    Optional<Task> storedTask = Optional.empty();
    when(taskRepository.findByName(any(String.class))).thenReturn(storedTask);
    // then
    taskQueryService.getByName(name);
  }

  @Test
  public void whenConsultingFinished_givenExistFinishedTasksOnRepository_thenRetrieve() {
    // given
    List<Task> storedTasks = taskFactory.generateFinishedList();
    // when
    when(taskRepository.findByFinished(true)).thenReturn(storedTasks);
    // then
    List<TaskResponse> tasks = taskQueryService.getFinished();

    assertResponseListMatchesRepositoryList(tasks, storedTasks);
  }

  @Test
  public void whenConsultingUnfinished_givenExistUnfinishedTasksOnRepository_thenRetrieve() {
    // given
    List<Task> storedTasks = taskFactory.generateUnfinishedList();
    // when
    when(taskRepository.findByFinished(false)).thenReturn(storedTasks);
    // then
    List<TaskResponse> tasks = taskQueryService.getUnfinished();

    assertResponseListMatchesRepositoryList(tasks, storedTasks);
  }


}
