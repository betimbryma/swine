package io.bryma.betim.swine.services;

import akka.actor.ActorSystem;
import org.springframework.stereotype.Service;

@Service
public class ActorService {

    private final ActorSystem actorSystem;

    public ActorService(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
    }


}
