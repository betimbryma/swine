package io.bryma.betim.swine.config;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import eu.smartsocietyproject.payment.PaymentService;
import eu.smartsocietyproject.pf.*;
import eu.smartsocietyproject.smartcom.SmartComServiceRestImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class SmartSocietyApplicationContextConfig {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private MongoDatabase mongoDatabase;

    @Bean
    public SmartSocietyApplicationContext getSmartSocietyApplicationContext() {

        CollectiveKindRegistry kindRegistry = CollectiveKindRegistry
                .builder().register(CollectiveKind.EMPTY).build();


        PeerManagerMongoProxy.Factory pmFactory
                = PeerManagerMongoProxy.factory(mongoDatabase);

        return new SmartSocietyApplicationContext(kindRegistry,
                pmFactory,
                new SmartComServiceRestImpl.Factory(), paymentService);
    }

}
