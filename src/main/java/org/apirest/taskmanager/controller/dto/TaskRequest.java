package org.apirest.taskmanager.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {

  @JsonProperty("name")
  @NotBlank(message = "'name' is mandatory")
  @Size(max = 50, message = "'name' length cannot be more than {max} characters")
  private String name;

  @JsonProperty("description")
  @Size(max = 250, message = "'description' length cannot be more than {max} characters")
  private String description;
}
