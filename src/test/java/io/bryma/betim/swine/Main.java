package io.bryma.betim.swine;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import eu.smartsocietyproject.pf.enummerations.State;

import java.io.IOException;

public class Main {
  public static void main(String[] args) throws IOException {

    ActorSystem actorSystem = ActorSystem.create("Test");

    ActorRef actorRef = actorSystem.actorOf(CollectiveBasedTaskTest.props());

    actorRef.tell(State.WAITING_FOR_PROVISIONING, ActorRef.noSender());

    System.out.println(">>> Press ENTER to exit <<<");
    System.in.read();

  }
}
