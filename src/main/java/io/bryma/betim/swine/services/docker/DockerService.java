package io.bryma.betim.swine.services.docker;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class DockerService {

    @Autowired
    private DockerClient dockerClient;

    @PostConstruct
    public void init(){
        //this.dockerClient = DockerClientBuilder.getInstance().build();
    }
}
