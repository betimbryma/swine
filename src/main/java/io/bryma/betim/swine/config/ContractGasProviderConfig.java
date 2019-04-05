package io.bryma.betim.swine.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;

@Configuration
public class ContractGasProviderConfig {

    @Value("${gasLimit}")
    private long gasLimit;
    @Value("${gasPrice}")
    private long gasPrice;

    @Bean
    public ContractGasProvider getContractGasProvider(){
        return new ContractGasProvider() {
            @Override
            public BigInteger getGasPrice(String s) {
                return BigInteger.valueOf(gasPrice);
            }

            @Override
            public BigInteger getGasPrice() {
                return BigInteger.valueOf(gasPrice);
            }

            @Override
            public BigInteger getGasLimit(String s) {
                return BigInteger.valueOf(gasLimit);
            }

            @Override
            public BigInteger getGasLimit() {
                return BigInteger.valueOf(gasLimit);
            }
        };
    }
}
