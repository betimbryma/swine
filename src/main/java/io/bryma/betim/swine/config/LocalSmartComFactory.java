package io.bryma.betim.swine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LocalSmartComFactory {
    @Bean
    public LocalSmartCom.Factory factory(LocalMail localMail) {
        return new LocalSmartCom.Factory(localMail);
    }
}
