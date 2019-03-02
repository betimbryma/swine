package io.bryma.betim.swine.services.Scenario1;

import at.ac.tuwien.dsg.MongoDBLauncher;
import at.ac.tuwien.dsg.PeerManagerConnector;
import at.ac.tuwien.dsg.peer.PeerMailboxService;
import at.ac.tuwien.dsg.peer.PeerMailboxServiceLauncher;
import at.ac.tuwien.dsg.pm.PeerManager;
import at.ac.tuwien.dsg.pm.PeerManagerLauncher;
import at.ac.tuwien.dsg.smartcom.SmartCom;
import at.ac.tuwien.dsg.smartcom.SmartComBuilder;
import at.ac.tuwien.dsg.smartcom.adapters.RESTInputAdapter;
import at.ac.tuwien.dsg.smartcom.callback.NotificationCallback;
import at.ac.tuwien.dsg.smartcom.exception.CommunicationException;
import at.ac.tuwien.dsg.smartcom.model.Identifier;

import at.ac.tuwien.dsg.smartcom.model.Message;
import at.ac.tuwien.dsg.smartcom.model.PeerChannelAddress;
import at.ac.tuwien.dsg.smartcom.rest.model.NotificationDTO;
import at.ac.tuwien.dsg.smartcom.rest.model.RoutingRuleDTO;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.mongodb.MongoClient;
import eu.smartsocietyproject.pf.*;
import eu.smartsocietyproject.pf.helper.InternalPeerManager;
import eu.smartsocietyproject.pf.helper.PeerIntermediary;
import eu.smartsocietyproject.runtime.Runtime;
import eu.smartsocietyproject.smartcom.PeerChannelAddressAdapter;
import eu.smartsocietyproject.smartcom.SmartComServiceImpl;
import eu.smartsocietyproject.smartcom.SmartComServiceRestImpl;
import io.bryma.betim.swine.web.SmartComController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import java.io.*;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

@Component
public class Scenario1 implements NotificationCallback {

	private static Runtime runtime;
	
	@Autowired
	private SmartComController smartComController;

	public void start() throws IOException, CommunicationException, InstantiationException {
		
		RunMailServer.start();

		CollectiveKindRegistry kindRegistry = CollectiveKindRegistry.builder().register(CollectiveKind.EMPTY).build();

		MongoRunner runner = MongoRunner.withPort(6666);

		PeerManagerMongoProxy.Factory pmFactory = PeerManagerMongoProxy.factory(runner.getMongoDb());

		MongoClient mongoClient = new MongoClient("localhost", 6666);

		SmartSocietyApplicationContext context = new SmartSocietyApplicationContext(kindRegistry, pmFactory, SmartComServiceRestImpl.factory());

		SmartComServiceRestImpl smartComService = (SmartComServiceRestImpl) context.getSmartCom();
		NotificationDTO notificationDTO = new NotificationDTO();

		notificationDTO.setUrl("http://localhost:9080/api/");
		smartComService.registerNotificationCallback(notificationDTO);

		Properties properties = new Properties();
		properties.load(Scenario1.class.getClassLoader()
			.getResourceAsStream("EmailAdapter.properties"));

		//smartComService.addPushAdapter(new RESTInputAdapter(9696, ""))

		Scenario1.runtime = new Runtime(context, new S1Application(context));
		Scenario1.runtime.run();
		loadPeers(context);
	}

	@Override
	public void notify(Message message) {
		System.out.println("test");
	}
	
	private static void loadPeers(SmartSocietyApplicationContext context) throws IOException {
        InternalPeerManager peerManager = (InternalPeerManager) context.getPeerManager();
        Gson gson = new Gson();
        List<io.bryma.betim.swine.model.Peer> peers = gson.fromJson(CharStreams.toString(new InputStreamReader(
                Thread.currentThread().getContextClassLoader().getResourceAsStream("Peers.json"), Charsets.UTF_8
        )),new TypeToken<List<io.bryma.betim.swine.model.Peer>>(){}.getType());

        peers.stream().forEach(peer ->
                peerManager.persistPeer(PeerIntermediary.builder(peer.getName(), peer.getRole())
                .addDeliveryAddress(PeerChannelAddressAdapter
                .convert(new PeerChannelAddress(
                        Identifier.peer(peer.getName()),
                        Identifier.channelType(peer.getChannelType()),
                        Arrays.asList(peer.getChannel())
                )
                )).addAttribute("restaurantQA", AttributeType.from("true")).build()

                ));
    }

	public static void main(String[] args) throws InstantiationException, IOException, CommunicationException {

		new Scenario1().start();

	}
}
