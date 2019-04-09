package io.bryma.betim.swine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.icegreen.greenmail.util.GreenMail;
import io.bryma.betim.swine.model.Peer;
import io.bryma.betim.swine.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

@SpringBootApplication
public class SwineApplication implements CommandLineRunner {

    @Value("${demo}")
    private boolean demo;
    @Autowired
    private PeerService peerService;
    @Autowired
    private GreenMail greenMail;


    public static void main(String[] args) {
        SpringApplication.run(SwineApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (demo) {

            List<Peer> peers = new Gson().fromJson(
                    new InputStreamReader(
                            Objects.requireNonNull(
                                    Thread.currentThread()
                                            .getContextClassLoader()
                                            .getResourceAsStream("Peers.json"))), new TypeToken<List<Peer>>() {
                    }.getType());

            peers.forEach(peer -> {
                try {
                    peerService.save(peer);
                    greenMail.setUser(peer.getEmail(), peer.getId(), peer.getId());
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });


        }
    }
}

