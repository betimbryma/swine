package io.bryma.betim.swine.repositories;

import io.bryma.betim.swine.model.Execution;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExecutionRepository extends MongoRepository<Execution, String> {
}
