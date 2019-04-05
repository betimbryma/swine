package io.bryma.betim.swine.services;

import at.ac.tuwien.dsg.smartcom.model.Identifier;
import at.ac.tuwien.dsg.smartcom.model.PeerChannelAddress;
import com.fasterxml.jackson.core.JsonProcessingException;
import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.pf.AttributeType;
import eu.smartsocietyproject.pf.Member;
import eu.smartsocietyproject.pf.Peer;
import eu.smartsocietyproject.pf.SmartSocietyApplicationContext;
import eu.smartsocietyproject.pf.helper.InternalPeerManager;
import eu.smartsocietyproject.pf.helper.PeerIntermediary;
import eu.smartsocietyproject.smartcom.ObjectMapperSingelton;
import io.bryma.betim.swine.repositories.PeerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;

@Service
public class PeerService implements UserDetailsService {

    private InternalPeerManager peerManager;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public PeerService(SmartSocietyApplicationContext smartSocietyApplicationContext, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.peerManager = (InternalPeerManager) smartSocietyApplicationContext.getPeerManager();
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        try {
            Peer peer = peerManager.retrievePeer(s);
            return new io.bryma.betim.swine.model.Peer(peer);
        } catch (PeerManagerException e) {
            throw new UsernameNotFoundException("Peer not found");
        }
    }


    public void save(io.bryma.betim.swine.model.Peer peer) throws JsonProcessingException {
        peerManager.persistPeer(PeerIntermediary
                .builder(peer.getId(), peer.getRole(), peer.getAddress())
                .addDeliveryAddress(AttributeType.from(ObjectMapperSingelton.getObjectMapper()
                        .writerWithDefaultPrettyPrinter().writeValueAsString(new PeerChannelAddress(
                                Identifier.peer(peer.getId()),
                                Identifier.channelType("email"),
                                Collections.singletonList(peer.getEmail()))
                        )
                ))
                .addAttribute("system", AttributeType.from("swine"))
                .addAttribute("email", AttributeType.from(peer.getEmail()))
                .addAttribute("address", AttributeType.from(peer.getAddress()))
                .addAttribute("location", AttributeType.from(peer.getLocation()))
                .addAttribute("password", AttributeType.from(bCryptPasswordEncoder.encode(peer.getPassword())))
                .build());
    }
}
