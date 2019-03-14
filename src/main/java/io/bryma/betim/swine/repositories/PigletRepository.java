package io.bryma.betim.swine.repositories;

import io.bryma.betim.swine.model.Peer;
import io.bryma.betim.swine.model.Piglet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PigletRepository extends MongoRepository<Piglet, String> {

    Optional<Piglet> findById(String id);
    Iterable<Piglet> findByOwner(Peer owner);

}
