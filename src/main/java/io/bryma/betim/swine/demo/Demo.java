package io.bryma.betim.swine.demo;

import io.bryma.betim.swine.model.Peer;
import io.bryma.betim.swine.model.Piglet;
import io.bryma.betim.swine.repositories.PeerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Null;
import java.util.Arrays;
import java.util.List;

@Component
public class Demo implements CommandLineRunner {

    private PeerRepository peerRepository;

    public Demo(PeerRepository peerRepository) {
        this.peerRepository = peerRepository;
    }

    @Override
    public void run(String... args) throws Exception {


        this.peerRepository.deleteAll();

        //List<Peer> peers = Arrays.asList(peer, peer2, peer3);

       // this.peerRepository.saveAll(peers);

    }

}
