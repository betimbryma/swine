package io.bryma.betim.swine.config;

import at.ac.tuwien.dsg.smartcom.exception.CommunicationException;
import at.ac.tuwien.dsg.smartcom.model.Message;
import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.pf.ResidentCollective;
import eu.smartsocietyproject.smartcom.SmartComService;

public class LocalSmartCom implements SmartComService {

    private LocalMail localMail;
    private PeerManager peerManager;

    public LocalSmartCom(LocalMail localMail, PeerManager peerManager) {
        this.localMail = localMail;
        this.peerManager = peerManager;
    }

    @Override
    public void send(Message msg) throws CommunicationException {

        String collectiveId = msg.getReceiverId().getId();
        try {
            ResidentCollective residentCollective = peerManager.readCollectiveById(collectiveId);
            residentCollective.getMembers():

        } catch (PeerManagerException e) {
            throw new CommunicationException(e.getMessage(), null);
        }

    }

    public static LocalSmartCom.Factory factory(LocalMail localMail) {
        return new LocalSmartCom.Factory(localMail);
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