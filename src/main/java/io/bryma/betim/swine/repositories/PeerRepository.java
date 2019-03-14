package io.bryma.betim.swine.repositories;

import io.bryma.betim.swine.model.Peer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeerRepository extends MongoRepository<Peer, String> {
}
