package io.bryma.betim.swine.repositories;

import io.bryma.betim.swine.model.Negotiation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NegotiationRepository extends CrudRepository<Negotiation, Long> {

}
