package io.bryma.betim.swine.repositories;

import io.bryma.betim.swine.model.QualityAssurance;
import io.bryma.betim.swine.model.QualityAssuranceInstance;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QualityAssuranceInstanceRepository extends CrudRepository<QualityAssuranceInstance, Long> {

    Optional<QualityAssuranceInstance> getByQualityAssuranceAndPeer(QualityAssurance qualityAssurance, String peer);

}
