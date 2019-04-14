package io.bryma.betim.swine;

import at.ac.tuwien.dsg.smartcom.exception.CommunicationException;
import at.ac.tuwien.dsg.smartcom.model.Message;
import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.smartcom.SmartComService;
import io.bryma.betim.swine.config.LocalMail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalSmartComTest implements SmartComService {

  private final Logger logger = LoggerFactory.getLogger("SMARTCOM");


  @Override
  public void send(Message msg) throws CommunicationException {

   logger.debug(msg.getContent());

  }

  public static class Factory implements SmartComService.Factory {


    @Override
    public SmartComService create(PeerManager pm) {

      return new LocalSmartComTest();
    }
  }
}