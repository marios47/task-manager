package org.apirest.taskmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

  private final String API_TITLE = "task-manager-api";
  private final String API_DESCRIPTION = "API to manage CRUD operations";
  private final String GROUP_NAME = "tasks";
  private final String API_VERSION = "v1";
  private final String API_BASE_PACKAGE = "org.apirest.taskmanager.controller";

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName(GROUP_NAME)
        .apiInfo(apiInfo())
        .select()
        .apis(RequestHandlerSelectors.basePackage(API_BASE_PACKAGE))
        .paths(PathSelectors.any())
        .build();
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder().title(API_TITLE)
        .description(API_DESCRIPTION)
        .version(API_VERSION)
        .build();
  }
}
