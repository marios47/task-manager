package org.apirest.taskmanager.repository;

import java.util.List;
import java.util.Optional;
import org.apirest.taskmanager.repository.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

  Optional<Task> findByName(String name);

  List<Task> findByFinished(Boolean finished);

}
