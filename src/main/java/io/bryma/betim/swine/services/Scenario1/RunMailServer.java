package io.bryma.betim.swine.services.Scenario1;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import io.bryma.betim.swine.model.Peer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Component
public class RunMailServer {

    private static Gson gson = new Gson();
    private static final GreenMail greenMail = new GreenMail(ServerSetupTest.ALL);

    public static void start() throws IOException {
        greenMail.start();
        List<Peer> peers = gson.fromJson(CharStreams.toString(new InputStreamReader(
                Thread.currentThread().getContextClassLoader().getResourceAsStream("Peers.json"), Charsets.UTF_8
        )),new TypeToken<List<Peer>>(){}.getType());

        peers.stream().filter(
                peer -> peer.getChannelType().equals("Email"))
                .forEach(peer -> greenMail.setUser(peer.getChannel(), peer.getName(), peer.getName()));

        greenMail.setUser("betim@localhost", "betim@localhost", "reputation");
    }
}
