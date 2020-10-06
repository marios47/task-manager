# Intro
Spring Boot microservice to perform CRUD operations on TASKS.
 
## Technologies
* Spring Boot
* Rest
* Spring Data JPA
* PostgreSQL

## Requirements
Requieres good connectivity with a PostgreSQL database.
If you have no PostgreSQL on your device, follow [this guide](database/DockerComposeInstructions.md) to start one using Docker Compose.

To create the tables in the database and get it ready to work follow [this guide](database/PreparingDatabase.md).

## Build & Launch
### Build
The project can be build using the Maven command

```mvn clean install```

Test can be skipped executing

```mvn clean install -Dmaven.test.skip=true```

or run on its own with

```mvn clean test```

### Launch
The project can be launched using the Spring Boot Maven command

```mvn spring-boot:run```

### Access to swagger user interface
When the service is ready, the swagger API can be accessed though
  
```localhost:9999/taskmanager/swagger-ui.html```

## Status
You can access _health_, _info_ or _loggers_ endpoints to check the status of the service

```localhost:9999/taskmanager/actuator/health```

```localhost:9999/taskmanager/actuator/info```

```localhost:9999/taskmanager/actuator/loggers```

## Monitoring
There are two endpoints available to get metrics: _/metrics_ and _/prometheus_

```localhost:9999/taskmanager/actuator/metrics```

```localhost:9999/taskmanager/actuator/prometheus```

These endpoints can be very useful when combined with monitoring tools like **Prometheus** or **Grafana** to
generate graphics.
 
## ChangeLog
The information with each update can be found on: [ChangeLog](CHANGELOG.md)