package io.bryma.betim.swine.demo;

import io.bryma.betim.swine.repositories.NegotiationRepository;
import io.bryma.betim.swine.repositories.PeerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class Demo implements CommandLineRunner {

    private PeerRepository peerRepository;
    @Autowired
    private NegotiationRepository negotiationRepository;

    public Demo(PeerRepository peerRepository) {
        this.peerRepository = peerRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        //this.negotiationRepository.save()

        //this.peerRepository.deleteAll();

        //List<Peer> peers = Arrays.asList(peer, peer2, peer3);

       // this.peerRepository.saveAll(peers);

    }

}
