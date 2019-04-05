package io.bryma.betim.swine.config;

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

    @Value("${peermanager.port}")
    private int port;
    @Autowired
    private PaymentService paymentService;

    @Bean
    public SmartSocietyApplicationContext getSmartSocietyApplicationContext() throws IOException {

        CollectiveKindRegistry kindRegistry = CollectiveKindRegistry
                .builder().register(CollectiveKind.EMPTY).build();
        MongoRunner runner = MongoRunner.withPort(port);

        PeerManagerMongoProxy.Factory pmFactory
                = PeerManagerMongoProxy.factory(runner.getMongoDb());

        return new SmartSocietyApplicationContext(kindRegistry,
                pmFactory,
                new SmartComServiceRestImpl.Factory(), paymentService);
    }

}
