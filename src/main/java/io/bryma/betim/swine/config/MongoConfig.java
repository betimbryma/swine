package io.bryma.betim.swine.config;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MongoConfig {

    @Value("${peermanager.port}")
    private int port;

    @Bean
    public MongoDatabase getMongoDatabase(){
        MongoClient client = new MongoClient("localhost", port);
        return client.getDatabase("peermanager");
    }


}
