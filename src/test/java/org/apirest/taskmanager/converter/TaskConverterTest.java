package org.apirest.taskmanager.converter;


import static org.assertj.core.api.Assertions.assertThat;

import org.apirest.taskmanager.controller.dto.TaskRequest;
import org.apirest.taskmanager.controller.dto.TaskResponse;
import org.apirest.taskmanager.repository.entities.Task;
import org.apirest.taskmanager.utils.TestRequestFactory;
import org.apirest.taskmanager.utils.TestTaskFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;

@RunWith(MockitoJUnitRunner.class)
public class TaskConverterTest {

  private TaskConverter taskConverter;
  private TestTaskFactory taskFactory;
  private TestRequestFactory requestFactory;

  @Before
  public void setUp() {
    this.taskConverter = Mappers.getMapper(TaskConverter.class);
    this.taskFactory = new TestTaskFactory();
    this.requestFactory = new TestRequestFactory();
  }

  @Test
  public void givenTaskRequest_whenMappingToEntity_thenAssertValuesMatch() {
    // given
    TaskRequest request = requestFactory.generate();
    // when
    Task entity = taskConverter.requestToEntity(request);
    // then
    assertThat(request).usingRecursiveComparison().isEqualTo(entity);
  }

  @Test
  public void givenTaskEntity_whenMappingToResponse_thenAssertValuesMatch() {
    // given
    Task entity = taskFactory.generate();
    // when
    TaskResponse response = taskConverter.entityToResponse(entity);
    // then
    assertThat(response).isEqualToComparingFieldByField(entity);
  }

  @Test
  public void givenPagedTaskEntity_whenMappingToPagedResponse_thenAssertValuesMatch() {
    // given
    Page<Task> taskPage = taskFactory.generatePage();
    // when
    Page<TaskResponse> responsePage = taskConverter.pagedEntityToPagedResponse(taskPage);
    // then
    assertThat(responsePage).usingRecursiveComparison().ignoringAllOverriddenEquals().isEqualTo(taskPage);
  }

}
