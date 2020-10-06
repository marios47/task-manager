package org.apirest.taskmanager.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.apirest.taskmanager.controller.dto.TaskRequest;
import org.apirest.taskmanager.controller.dto.TaskResponse;
import org.apirest.taskmanager.exceptions.TaskErrorHandler;
import org.apirest.taskmanager.exceptions.TaskException;
import org.apirest.taskmanager.service.CommandService;
import org.apirest.taskmanager.service.QueryService;
import org.apirest.taskmanager.utils.TestRequestFactory;
import org.apirest.taskmanager.utils.TestResponseFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(MockitoJUnitRunner.class)
public class TaskControllerTest {

  private final String EMPTY_NAME = "";
  private final String LONG_NAME = "Looooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong name";
  private final String LONG_DESCRIPTION = "Very, very loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong description";

  @Mock
  private QueryService queryService;
  @Mock
  private CommandService commandService;

  @InjectMocks
  private TaskController taskController;

  ObjectMapper mapper = new ObjectMapper();
  private final TestResponseFactory responseFactory = new TestResponseFactory();
  private final TestRequestFactory requestFactory = new TestRequestFactory();
  private MockMvc mockMvc;

  @Before
  public void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(taskController).setControllerAdvice(TaskErrorHandler.class).build();
  }

  @Test
  public void whenGetOperation_givenNoParameters_thenRetrieveAllTasks() throws Exception {
    // given
    String path = "/tasks";
    // when
    List<TaskResponse> storedTasks = responseFactory.generateList();
    when(queryService.getAll()).thenReturn(storedTasks);
    // then
    ResultActions results = mockMvc.perform(get(path))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    List<TaskResponse> tasks = mapper.readValue(results.andReturn().getResponse().getContentAsString(),
        new TypeReference<List<TaskResponse>>() {
        });

    assertThat(tasks).containsExactlyInAnyOrderElementsOf(storedTasks);
  }

  @Test
  public void whenGetOperation_givenPageAndSizeParameters_thenRetrievePaginatedTasks() throws Exception {
    // given
    int page = 1;
    int size = 10;
    String path = "/tasks";
    // when
    Page<TaskResponse> storedTaskPage = responseFactory.generatePage(page, size);
    when(queryService.getAll(any(int.class), any(int.class))).thenReturn(storedTaskPage);
    // then
    mockMvc.perform(get(path)
        .param("page", String.valueOf(page))
        .param("size", String.valueOf(size)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    // Deserialization of Page<T> fails. Thus we are going to test taskController directly
    ResponseEntity<Page<TaskResponse>> taskResponseEntity = taskController.findPage(page, size);
    assertThat(taskResponseEntity.getBody()).usingRecursiveComparison().isEqualTo(storedTaskPage);
  }

  @Test
  public void whenGetOperation_givenWrongPageParameter_thenReturnBadRequest() throws Exception {
    // given
    String page = "badPage";
    int size = 10;
    String path = "/tasks";
    // when

    // then
    ResultActions results = mockMvc.perform(get(path)
        .param("page", page)
        .param("size", String.valueOf(size)))
        .andExpect(status().isBadRequest());
    String response = results.andReturn().getResponse().getContentAsString();

    assertThat(response).isEqualTo("The value '" + page + "' provided is not correct. Please provide a number");
  }

  @Test
  public void whenGetOperation_givenWrongSizeParameter_thenReturnBadRequest() throws Exception {
    // given
    int page = 1;
    String size = "badSize";
    String path = "/tasks";
    // when

    // then
    ResultActions results = mockMvc.perform(get(path)
        .param("page", String.valueOf(page))
        .param("size", size))
        .andExpect(status().isBadRequest());
    String response = results.andReturn().getResponse().getContentAsString();

    assertThat(response).isEqualTo("The value '" + size + "' provided is not correct. Please provide a number");
  }

  @Test
  public void whenGetOperation_givenIdOnPath_thenRetrieveThatTask() throws Exception {
    // given
    long id = 1L;
    String path = "/tasks/" + id;
    // when
    TaskResponse storedTask = responseFactory.generateWithId(id);
    when(queryService.getById(id)).thenReturn(storedTask);
    // then
    ResultActions results = mockMvc.perform(get(path))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    TaskResponse task = mapper.readValue(results.andReturn().getResponse().getContentAsString(), TaskResponse.class);

    assertThat(task).usingRecursiveComparison().isEqualTo(storedTask);
  }

  @Test
  public void whenGetOperation_givenIdOnPathThatNotExistOnDatabase_thenReturnErrorResponse() throws Exception {
    // given
    long id = 1L;
    String path = "/tasks/" + id;
    // when
    when(queryService.getById(id)).thenThrow(TaskException.idNotFound(id));
    // then
    ResultActions results = mockMvc.perform(get(path))
        .andExpect(status().isNotFound());
    String response = results.andReturn().getResponse().getContentAsString();

    assertThat(response).isEqualTo("Task with id '" + id + "' not found");
  }

  @Test
  public void whenGetOperation_givenWrongIdOnPathThatNotExistOnDatabase_thenReturnErrorResponse() throws Exception {
    // given
    String id = "badId";
    String path = "/tasks/" + id;
    // when

    // then
    ResultActions results = mockMvc.perform(get(path))
        .andExpect(status().isBadRequest());
    String response = results.andReturn().getResponse().getContentAsString();

    assertThat(response).isEqualTo("The value '" + id + "' provided is not correct. Please provide a number");
  }

  @Test
  public void whenGetOperation_givenNameOnPath_thenRetrieveThatTask() throws Exception {
    // given
    String name = "taskName";
    String path = "/tasks/name/" + name;
    // when
    TaskResponse storedTask = responseFactory.generateWithName(name);
    when(queryService.getByName(name)).thenReturn(storedTask);
    // then
    ResultActions results = mockMvc.perform(get(path))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    TaskResponse task = mapper.readValue(results.andReturn().getResponse().getContentAsString(), TaskResponse.class);

    assertThat(task).usingRecursiveComparison().isEqualTo(storedTask);
  }

  @Test
  public void whenGetOperation_givenNameOnPathThatNotExistOnDatabase_thenReturnErrorResponse() throws Exception {
    // given
    String name = "taskName";
    String path = "/tasks/name/" + name;
    // when
    when(queryService.getByName(name)).thenThrow(TaskException.nameNotFound(name));
    // then
    ResultActions results = mockMvc.perform(get(path))
        .andExpect(status().isNotFound());
    String response = results.andReturn().getResponse().getContentAsString();

    assertThat(response).isEqualTo("Task with name '" + name + "' not found");
  }

  @Test
  public void whenGetOperation_givenFinishedOnPath_thenReturnFinishedTasks() throws Exception {
    // given
    String path = "/tasks/finished/";
    // when
    List<TaskResponse> storedTasks = responseFactory.generateFinishedList();
    when(queryService.getFinished()).thenReturn(storedTasks);
    // then
    ResultActions results = mockMvc.perform(get(path))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    List<TaskResponse> tasks = mapper.readValue(results.andReturn().getResponse().getContentAsString(),
        new TypeReference<List<TaskResponse>>() {
        });

    assertThat(tasks).containsExactlyInAnyOrderElementsOf(storedTasks);
  }

  @Test
  public void whenGetOperation_givenUnfinishedOnPath_thenReturnFinishedTasks() throws Exception {
    // given
    String path = "/tasks/unfinished/";
    // when
    List<TaskResponse> storedTasks = responseFactory.generateUnfinishedList();
    when(queryService.getUnfinished()).thenReturn(storedTasks);
    // then
    ResultActions results = mockMvc.perform(get(path))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    List<TaskResponse> tasks = mapper.readValue(results.andReturn().getResponse().getContentAsString(),
        new TypeReference<List<TaskResponse>>() {
        });

    assertThat(tasks).containsExactlyInAnyOrderElementsOf(storedTasks);
  }


  @Test
  public void whenPostOperation_givenTaskRequest_thenCreate() throws Exception {
    // given
    String path = "/tasks";
    TaskRequest taskRequest = requestFactory.generate();
    // when
    TaskResponse storedTask =
        TaskResponse.builder().id(1L).name(taskRequest.getName()).description(taskRequest.getDescription()).build();
    when(commandService.create(any(TaskRequest.class))).thenReturn(storedTask);
    // then
    ResultActions results = mockMvc.perform(post(path)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(taskRequest)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    TaskResponse task = mapper.readValue(results.andReturn().getResponse().getContentAsString(), TaskResponse.class);

    assertThat(taskRequest).usingRecursiveComparison().isEqualTo(task);
  }

  @Test
  public void whenPostOperation_givenTaskRequestBlankName_thenThrowBadRequestException() throws Exception {
    // given
    String path = "/tasks";
    // when
    TaskRequest taskRequest = requestFactory.generateWithName(EMPTY_NAME);
    // then
    ResultActions results = mockMvc.perform(post(path)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(taskRequest)))
        .andExpect(status().isBadRequest());

    String response = results.andReturn().getResponse().getContentAsString();

    assertThat(response).isEqualTo("Error on field 'name': 'name' is mandatory");
  }

  @Test
  public void whenPostOperation_givenTaskRequestNameTooLong_thenThrowBadRequestException() throws Exception {
    // given
    String path = "/tasks";
    // when
    TaskRequest taskRequest = requestFactory.generateWithName(LONG_NAME);
    // then
    ResultActions results = mockMvc.perform(post(path)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(taskRequest)))
        .andExpect(status().isBadRequest());

    String response = results.andReturn().getResponse().getContentAsString();

    assertThat(response).isEqualTo("Error on field 'name': 'name' length cannot be more than 50 characters");
  }

  @Test
  public void whenPostOperation_givenTaskRequestDescriptionTooLong_thenThrowBadRequestException() throws Exception {
    // given
    String path = "/tasks";
    // when
    TaskRequest taskRequest = requestFactory.generateWithDescription(LONG_DESCRIPTION);
    // then
    ResultActions results = mockMvc.perform(post(path)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(taskRequest)))
        .andExpect(status().isBadRequest());

    String response = results.andReturn().getResponse().getContentAsString();

    assertThat(response)
        .isEqualTo("Error on field 'description': 'description' length cannot be more than 250 characters");
  }

  @Test
  public void whenPutOperation_givenTaskRequest_thenUpdate() throws Exception {
    // given
    long id = 1L;
    String path = "/tasks/" + id;
    // when
    TaskRequest taskRequest = requestFactory.generate();
    TaskResponse storedTask =
        TaskResponse.builder().id(id).name(taskRequest.getName()).description(taskRequest.getDescription()).build();
    when(commandService.update(any(long.class), any(TaskRequest.class))).thenReturn(storedTask);
    // then
    ResultActions results = mockMvc.perform(put(path)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(taskRequest)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    TaskResponse task = mapper.readValue(results.andReturn().getResponse().getContentAsString(), TaskResponse.class);

    assertThat(taskRequest).usingRecursiveComparison().isEqualTo(task);
  }

  @Test
  public void whenPutOperation_givenIdOnPathThatNotExistOnDatabase_thenReturnErrorResponse() throws Exception {
    // given
    long id = 1L;
    String path = "/tasks/" + id;
    // when
    TaskRequest taskRequest = requestFactory.generate();
    when(commandService.update(any(long.class), any(TaskRequest.class))).thenThrow(TaskException.idNotFound(id));
    // then
    ResultActions results = mockMvc.perform(put(path)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(taskRequest)))
        .andExpect(status().isNotFound());
    String response = results.andReturn().getResponse().getContentAsString();

    assertThat(response).isEqualTo("Task with id '" + id + "' not found");
  }

  @Test
  public void whenPutOperation_givenTaskRequestBlankName_thenThrowBadRequestException() throws Exception {
    // given
    long id = 1L;
    String path = "/tasks/" + id;
    // when
    TaskRequest taskRequest = requestFactory.generateWithName(EMPTY_NAME);
    // then
    ResultActions results = mockMvc.perform(put(path)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(taskRequest)))
        .andExpect(status().isBadRequest());

    String response = results.andReturn().getResponse().getContentAsString();

    assertThat(response).isEqualTo("Error on field 'name': 'name' is mandatory");
  }

  @Test
  public void whenPutOperation_givenTaskRequestNameTooLong_thenThrowBadRequestException() throws Exception {
    // given
    long id = 1L;
    String path = "/tasks/" + id;
    // when
    TaskRequest taskRequest = requestFactory.generateWithName(LONG_NAME);
    // then
    ResultActions results = mockMvc.perform(put(path)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(taskRequest)))
        .andExpect(status().isBadRequest());

    String response = results.andReturn().getResponse().getContentAsString();

    assertThat(response).isEqualTo("Error on field 'name': 'name' length cannot be more than 50 characters");
  }

  @Test
  public void whenPutOperation_givenTaskRequestDescriptionTooLong_thenThrowBadRequestException() throws Exception {
    // given
    long id = 1L;
    String path = "/tasks/" + id;
    // when
    TaskRequest taskRequest = requestFactory.generateWithDescription(LONG_DESCRIPTION);
    // then
    ResultActions results = mockMvc.perform(put(path)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(taskRequest)))
        .andExpect(status().isBadRequest());

    String response = results.andReturn().getResponse().getContentAsString();

    assertThat(response)
        .isEqualTo("Error on field 'description': 'description' length cannot be more than 250 characters");
  }

  @Test
  public void whenDeleteOperation_givenid_thenDelete() throws Exception {
    // given
    long id = 1L;
    String path = "/tasks/" + id;
    // when

    // then
    mockMvc.perform(delete(path))
        .andExpect(status().isAccepted());

    verify(commandService).delete(id);
  }

  @Test
  public void whenDeleteOperation_givenIdOnPathThatNotExistOnDatabase_thenReturnErrorResponse() throws Exception {
    // given
    long id = 1L;
    String path = "/tasks/" + id;
    // when
    doThrow(new EmptyResultDataAccessException(1)).when(commandService).delete(id);
    // then
    ResultActions results = mockMvc.perform(delete(path))
        .andExpect(status().isNotFound());
    String response = results.andReturn().getResponse().getContentAsString();

    assertThat(response).isEqualTo("Task not found");
    verify(commandService).delete(id);
  }

  @Test
  public void whenFinishOperation_givenIdOnPathThatExistsOnDatabase_thenFinishTask() throws Exception {
    // given
    long id = 1L;
    String path = "/tasks/finish/" + id;
    // when

    // then
    mockMvc.perform(get(path))
        .andExpect(status().isAccepted());

    verify(commandService).finish(id);
  }

  @Test
  public void whenFinishOperation_givenIdOnPathNotExistsOnDatabase_thenReturnErrorResponse() throws Exception {
    // given
    long id = 1L;
    String path = "/tasks/" + id;
    // when
    doThrow(TaskException.idNotFound(id)).when(commandService).delete(id);
    // then

    ResultActions results = mockMvc.perform(delete(path))
        .andExpect(status().isNotFound());
    String response = results.andReturn().getResponse().getContentAsString();

    assertThat(response).isEqualTo("Task with id '" + id + "' not found");
  }

}
