package org.apirest.taskmanager.utils;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import io.github.benas.randombeans.randomizers.range.IntegerRangeRandomizer;
import org.apirest.taskmanager.controller.dto.TaskRequest;

public class TestRequestFactory {

  private EnhancedRandom rn;

  public TestRequestFactory() {
    this.rn = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
        .randomize(Integer.class, new IntegerRangeRandomizer(0, 50))
        .collectionSizeRange(1, 10)
        .objectPoolSize(50) // To avoid getting repeated values
        .build();
  }

  public TaskRequest generate() {
    return rn.nextObject(TaskRequest.class);
  }

  public TaskRequest generateWithName(String name) {
    TaskRequest request = generate();
    request.setName(name);
    return request;
  }

  public TaskRequest generateWithDescription(String description) {
    TaskRequest request = generate();
    request.setDescription(description);
    return request;
  }

}
