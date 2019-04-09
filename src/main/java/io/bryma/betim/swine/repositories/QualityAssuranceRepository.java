package io.bryma.betim.swine.repositories;

import io.bryma.betim.swine.model.QualityAssurance;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QualityAssuranceRepository extends CrudRepository<QualityAssurance, Long> {
}
