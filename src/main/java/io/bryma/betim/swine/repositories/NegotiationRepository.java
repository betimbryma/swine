package io.bryma.betim.swine.repositories;

import io.bryma.betim.swine.model.Negotiation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NegotiationRepository extends MongoRepository<Negotiation, String> {

}
