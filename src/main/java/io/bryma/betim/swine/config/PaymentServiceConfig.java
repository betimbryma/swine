package io.bryma.betim.swine.config;

import eu.smartsocietyproject.payment.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.tx.gas.ContractGasProvider;
import service.PaymentServiceImpl;

import java.math.BigInteger;

@Configuration
public class PaymentServiceConfig {

    @Value("${ethereum.node}")
    private String etheremNode;
    @Value("${etherem.key}")
    private String ethereumKey;
    @Value("${minimumPayment}")
    private long minimumPayment;
    @Autowired
    private ContractGasProvider contractGasProvider;

    @Bean
    public PaymentService getPaymentService(){

        Credentials credentials = Credentials.create(ethereumKey);

        return new PaymentServiceImpl(etheremNode, contractGasProvider,
                credentials, BigInteger.valueOf(minimumPayment));

    }
}
