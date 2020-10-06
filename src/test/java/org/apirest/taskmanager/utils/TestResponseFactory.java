package org.apirest.taskmanager.utils;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import io.github.benas.randombeans.randomizers.range.IntegerRangeRandomizer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.apirest.taskmanager.controller.dto.TaskResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

public class TestResponseFactory {

  private final EnhancedRandom rn;

  public TestResponseFactory() {
    this.rn = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
        .randomize(Integer.class, new IntegerRangeRandomizer(0, 50))
        .collectionSizeRange(1, 10)
        .objectPoolSize(50) // To avoid getting repeated values
        .build();
  }

  public TaskResponse generate() {
    return rn.nextObject(TaskResponse.class);
  }

  public TaskResponse generateWithId(Long id) {
    TaskResponse task = generate();
    task.setId(id);
    return task;
  }

  public TaskResponse generateWithName(String name) {
    TaskResponse task = generate();
    task.setName(name);
    return task;
  }

  public List<TaskResponse> generateList() {
    return generateList(rn.nextObject(Integer.class));
  }

  public List<TaskResponse> generateList(int length) {
    return rn.randomListOf(length, TaskResponse.class);
  }

  public Page<TaskResponse> generatePage(int page, int size) {
    return new PageImpl<>(
        generateList(size),
        PageRequest.of(page, size),
        size
    );
  }

  public TaskResponse generateFinished() {
    TaskResponse task = generate();
    task.setFinished(true);
    return task;

  }

  public List<TaskResponse> generateFinishedList() {
    return generateFinishedList(rn.nextObject(Integer.class));
  }

  public List<TaskResponse> generateFinishedList(int length) {
    List<TaskResponse> list = new ArrayList<>();
    IntStream.range(0, length - 1).forEach(i -> list.add(generateFinished()));
    return list;
  }

  public TaskResponse generateUnfinished() {
    TaskResponse task = generate();
    task.setFinished(false);
    return task;
  }

  public List<TaskResponse> generateUnfinishedList() {
    return generateUnfinishedList(rn.nextObject(Integer.class));
  }

  public List<TaskResponse> generateUnfinishedList(int length) {
    List<TaskResponse> list = new ArrayList<>();
    IntStream.range(0, length - 1).forEach(i -> list.add(generateUnfinished()));
    return list;
  }

}
