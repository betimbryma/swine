package io.bryma.betim.swine.repositories;

import io.bryma.betim.swine.model.Execution;
import io.bryma.betim.swine.model.TaskResult;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskResultRepository extends CrudRepository<TaskResult, Long> {
    Optional<TaskResult> getByExecutionAndPeer(Execution execution, String peer);
}
