package io.bryma.betim.swine.repositories;

import io.bryma.betim.swine.model.Peer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PeerRepository extends MongoRepository<Peer, String> {
}
