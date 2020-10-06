package org.apirest.taskmanager.utils;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import io.github.benas.randombeans.randomizers.range.IntegerRangeRandomizer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.apirest.taskmanager.repository.entities.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

public class TestTaskFactory {

  private EnhancedRandom rn;

  public TestTaskFactory() {
    this.rn = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
        .randomize(Integer.class, new IntegerRangeRandomizer(0, 50))
        .collectionSizeRange(1, 10)
        .objectPoolSize(50) // To avoid getting repeated values
        .build();
  }

  public Task generate() {
    return rn.nextObject(Task.class);
  }

  public Task generateWithId(Long id) {
    Task task = rn.nextObject(Task.class);
    task.setId(id);
    return task;
  }

  public Task generateWithName(String name) {
    Task task = rn.nextObject(Task.class);
    task.setName(name);
    return task;
  }


  public List<Task> generateList() {
    return generateList(rn.nextObject(Integer.class));
  }

  public List<Task> generateList(int length) {
    return rn.randomListOf(length, Task.class);
  }

  public Page<Task> generatePage() {
    return generatePage(rn.nextObject(Integer.class), rn.nextObject(Integer.class));
  }

  public Page<Task> generatePage(int page, int size) {
    return new PageImpl<>(
        generateList(size),
        PageRequest.of(page, size),
        size
    );
  }

  public Task generateFinished() {
    Task task = rn.nextObject(Task.class);
    task.setFinished(true);
    return task;
  }

  public List<Task> generateFinishedList() {
    return generateFinishedList(rn.nextObject(Integer.class));
  }

  public List<Task> generateFinishedList(int length) {
    List<Task> list = new ArrayList<>();
    IntStream.range(0, length - 1).forEach(i -> list.add(this.generateFinished()));
    return list;
  }

  public Task generateUnfinished() {
    Task task = rn.nextObject(Task.class);
    task.setFinished(false);
    return task;
  }

  public List<Task> generateUnfinishedList() {
    return generateUnfinishedList(rn.nextObject(Integer.class));
  }

  public List<Task> generateUnfinishedList(int length) {
    List<Task> list = new ArrayList<>();
    IntStream.range(0, length - 1).forEach(i -> list.add(generateUnfinished()));
    return list;
  }

}
