package io.bryma.betim.swine.piglet;

import com.typesafe.config.Config;
import eu.smartsocietyproject.pf.*;

import java.util.Set;

public class PigletApplication extends Application {

    private ApplicationContext applicationContext;
    private Config config;

    private String applicationId;

    public PigletApplication() {
        super();
    }

    @Override
    public String getApplicationId() {
        return this.applicationId;
    }

    public void setApplicationId(String applicationId){
        this.applicationId = applicationId;
    }

    @Override
    public void init(ApplicationContext context, Config config) {
        this.applicationContext = context;
        this.config = config;
    }

    @Override
    public Set<CollectiveKind> listCollectiveKinds() {
        return null;
    }

    @Override
    public TaskRequest createTaskRequest(TaskDefinition definition) throws ApplicationException {
        return new PigletTaskRequest(definition, getApplicationId());
    }

    @Override
    public TaskRunner createTaskRunner(TaskRequest request) {
        return new PigletTaskRunner();
    }
}
