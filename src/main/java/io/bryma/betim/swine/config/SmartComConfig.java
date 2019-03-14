package io.bryma.betim.swine.config;

import com.mongodb.MongoClient;
import eu.smartsocietyproject.smartcom.SmartComServiceRestImpl;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestOperations;

import java.time.Duration;

@Configuration
public class SmartComConfig {

    @Bean
    public SmartComServiceRestImpl getSmartComServiceImpl(final RestTemplateBuilder restTemplateBuilder){
        RestOperations restOperations = restTemplateBuilder.setReadTimeout(Duration.ofSeconds(10L))
                .setConnectTimeout(Duration.ofSeconds(10L)).build();
        return new SmartComServiceRestImpl(restOperations);
    }

}
