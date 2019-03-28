package io.bryma.betim.swine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SwineApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwineApplication.class, args);
    }
/*
    @Autowired
    private ExecutionService executionService;
    @Autowired
    private NegotiationService negotiationService;
    @Autowired
    private ExecutionRepository executionRepository;
    @Autowired
    private NegotiationRepository negotiationRepository;
    @Autowired
    private PeerRepository peerRepository;
    @Autowired
    private PigletRepository pigletRepository;
    @Autowired
    private ActorSystem actorSystem;

    //@Override
    public void run(String... args) throws Exception {

        CollectiveKindRegistry kindRegistry = CollectiveKindRegistry
                .builder().register(CollectiveKind.EMPTY).build();
        MongoRunner runner = MongoRunner.withPort(6668);
        PeerManagerMongoProxy.Factory pmFactory
                = PeerManagerMongoProxy.factory(runner.getMongoDb());
        SmartSocietyApplicationContext context
                = new SmartSocietyApplicationContext(kindRegistry,
                pmFactory,
                new SmartComServiceRestImpl.Factory());

        PeerLoader.laodPeers(context);


        Peer peer = new Peer();
        peer.setRole("HumanExpert");
        peer = peerRepository.save(peer);

        Piglet piglet = new Piglet();
        piglet.setOwner(peer);
        piglet.setDescription("Test piglet");
        piglet = pigletRepository.save(piglet);
        Negotiation negotiation = new Negotiation();
        negotiation.setCollectiveID("collectivetest");
        negotiation.setDone(true);
        negotiation.setId("aj di");
        negotiation.setType("test");

        negotiationRepository.save(negotiation);

        Execution execution = new Execution();
        execution.setState(PigletState.RUNNING);
        execution.setActorPath("home");
        execution.setCollectiveId("i dont care at all");
        execution.setEndDate(new Date());
        execution.setOwnerId("betim brajma");
        execution = executionRepository.save(execution);
        piglet = pigletRepository.findById(piglet.getId()).get();
        piglet.getCollectiveBasedTasks().add(execution);

        pigletRepository.save(piglet);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree("{\"task:\":\"local news\"}");
        TaskDefinition taskDefinition = new TaskDefinition(actualObj);

        PigletTaskRequest pigletTaskRequest = new PigletTaskRequest(taskDefinition, "swine");

        actorSystem.actorOf(PigletTaskRunner.props(pigletTaskRequest, context,
                "Vienna", negotiationService, executionService, "localhost", Duration.ofSeconds(30), Duration.ofMinutes(2), execution.getId()));

    }

    public static class PeerLoader {
        private static final ObjectMapper mapper = new ObjectMapper();

        public static void laodPeers(SmartSocietyApplicationContext context) throws IOException {

            InternalPeerManager pm = (InternalPeerManager) context.getPeerManager();

            List<JsonPeer> peers = mapper.readValue(Thread.currentThread()
                            .getContextClassLoader()
                            .getResourceAsStream("Peers.json"),
                    mapper.getTypeFactory()
                            .constructCollectionType(List.class, JsonPeer.class));

            peers.stream().forEach(peer -> {
                try {
                    pm.persistPeer(PeerIntermediary
                            .builder(peer.getName(), peer.getRole())
                            .addDeliveryAddress(AttributeType.from(ObjectMapperSingelton.getObjectMapper()
                                    .writerWithDefaultPrettyPrinter().writeValueAsString(new PeerChannelAddress(
                                            Identifier.peer(peer.getName()),
                                            Identifier.channelType(peer.getChannelType()),
                                            Arrays.asList(peer.getChannel()))
                                    )
                            ))
                            .addAttribute("system", AttributeType.from("swine"))
                            .addAttribute("location", AttributeType.from(peer.getLocation()))
                            .build());
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });

        }
    }

    public static class JsonPeer {
        private String name;
        private String channelType;
        private String channel;
        private String role;
        private String location;

        public String getName() {
            return name;
        }

        public String getChannelType() {
            return channelType;
        }

        public String getChannel() {
            return channel;
        }

        public String getRole() {
            return role;
        }

        public String getLocation() {return location;}

    } */
}

