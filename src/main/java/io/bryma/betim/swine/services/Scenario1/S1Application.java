package io.bryma.betim.swine.services.Scenario1;

import com.google.common.collect.ImmutableSet;
import com.typesafe.config.Config;
import eu.smartsocietyproject.pf.*;

import java.util.Set;

public class S1Application extends Application {

    private SmartSocietyApplicationContext applicationContext;
    private Config config;

    public S1Application() {

    }

    public S1Application(SmartSocietyApplicationContext smartSocietyApplicationContext){
        this.applicationContext = smartSocietyApplicationContext;
    }

    @Override
    public String getApplicationId() {
        return "Betim's Scenario 1";
    }

    @Override
    public void init(ApplicationContext applicationContext, Config config) {
        //this.applicationContext = applicationContext;
        this.config = config;
    }

    @Override
    public Set<CollectiveKind> listCollectiveKinds() {

        return ImmutableSet.of(
                CollectiveKind.builder("empty").build()
        );
    }

    @Override
    public TaskRequest createTaskRequest(TaskDefinition taskDefinition) throws ApplicationException {
        return new S1TaskRequest((S1TaskDefinition) taskDefinition);
    }

    @Override
    public TaskRunner createTaskRunner(TaskRequest taskRequest) {
        return new S1TaskRunner((S1TaskRequest)taskRequest, applicationContext);
    }


}