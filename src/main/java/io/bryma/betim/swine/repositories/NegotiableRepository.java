package io.bryma.betim.swine.repositories;

import io.bryma.betim.swine.model.Negotiable;
import io.bryma.betim.swine.model.Negotiation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NegotiableRepository extends CrudRepository<Negotiable, Long> {
    Optional<Negotiable> getByNegotiationAndPeer(Negotiation negotiation, String peer);
}
