package io.bryma.betim.swine.config;

import akka.actor.ActorSystem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActorConfig {

    @Bean
    public ActorSystem getActorSystem(){
        return ActorSystem.create("swine");
    }

}
