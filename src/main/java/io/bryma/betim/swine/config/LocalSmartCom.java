package io.bryma.betim.swine.config;

import at.ac.tuwien.dsg.smartcom.exception.CommunicationException;
import at.ac.tuwien.dsg.smartcom.model.Message;
import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.pf.Attribute;
import eu.smartsocietyproject.pf.AttributeType;
import eu.smartsocietyproject.pf.Member;
import eu.smartsocietyproject.pf.ResidentCollective;
import eu.smartsocietyproject.pf.helper.InternalPeerManager;
import eu.smartsocietyproject.pf.helper.PeerIntermediary;
import eu.smartsocietyproject.smartcom.SmartComService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.mail.MessagingException;
import java.util.Set;

public class LocalSmartCom implements SmartComService {

    private LocalMail localMail;
    private PeerManager peerManager;
    private final Logger logger = LoggerFactory.getLogger("SMARTCOM");

    private LocalSmartCom(LocalMail localMail, PeerManager peerManager) {
        this.localMail = localMail;
        this.peerManager = peerManager;
    }

    @Override
    public void send(Message msg) throws CommunicationException {

        String collectiveId = msg.getReceiverId().getId();
        try {
            ResidentCollective residentCollective = peerManager.readCollectiveById(collectiveId);
            Set<Member> members = residentCollective.getMembers();
            InternalPeerManager internalPeerManager = (InternalPeerManager) peerManager;
            members.stream().forEach(member -> {
                try {
                    PeerIntermediary peerIntermediary = internalPeerManager.readPeerById(member.getPeerId());
                    String email = peerIntermediary.getAttribute("deliveryAddress").orElse(AttributeType.from("")).toJson().textValue();
                    localMail.sendMail(email, msg.getType(), msg.getContent());
                } catch (PeerManagerException | MessagingException e) {
                    logger.error(e.getMessage());
                }

            });

        } catch (PeerManagerException e) {
            throw new CommunicationException(e.getMessage(), null);
        }

    }

    public static class Factory implements SmartComService.Factory {

        private LocalMail localMail;

        Factory(LocalMail localMail){
            this.localMail = localMail;
        }

        @Override
        public SmartComService create(PeerManager pm) {

            return new LocalSmartCom(localMail, pm);
        }
    }
}