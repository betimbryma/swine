package io.bryma.betim.swine.config;

import eu.smartsocietyproject.pf.MongoRunner;
import eu.smartsocietyproject.pf.PeerManagerMongoProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class PeerManagerFactoryConfig {

    @Bean
    public PeerManagerMongoProxy.Factory getPMFactory() throws IOException {
        MongoRunner runner = MongoRunner.withPort(27017);
        return PeerManagerMongoProxy.factory(runner.getMongoDb());
    }

}
