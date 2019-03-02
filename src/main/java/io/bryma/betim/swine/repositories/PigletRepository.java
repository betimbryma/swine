package io.bryma.betim.swine.repositories;

import io.bryma.betim.swine.model.Piglet;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PigletRepository extends MongoRepository<Piglet, String> {
}
