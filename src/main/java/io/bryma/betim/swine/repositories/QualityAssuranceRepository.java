package io.bryma.betim.swine.repositories;

import io.bryma.betim.swine.model.QualityAssurance;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QualityAssuranceRepository extends MongoRepository<QualityAssurance, String> {
}
