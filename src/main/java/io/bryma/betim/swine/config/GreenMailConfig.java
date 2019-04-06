package io.bryma.betim.swine.config;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GreenMailConfig {

    @Value("${username}")
    private String username;
    @Value("${password}")
    private String password;

    @Bean
    public GreenMail getGreenMail(){

        GreenMail greenMail = new GreenMail(ServerSetupTest.ALL);

        greenMail.start();

        greenMail.setUser(username, username, password);


        return greenMail;
    }
}
