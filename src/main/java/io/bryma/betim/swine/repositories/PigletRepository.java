package io.bryma.betim.swine.repositories;

import io.bryma.betim.swine.model.Peer;
import io.bryma.betim.swine.model.Piglet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PigletRepository extends CrudRepository<Piglet, Long> {
    List<Piglet> findByOwner(String owner);
}
