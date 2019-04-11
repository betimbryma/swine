package io.bryma.betim.swine.repositories;

import io.bryma.betim.swine.model.Execution;
import io.bryma.betim.swine.model.Piglet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExecutionRepository extends CrudRepository<Execution, Long> {
    Optional<List<Execution>> getAllByPiglet(Piglet piglet);
    Optional<Execution> getExecutionByIdAndPeer(long id, String peer);
}
