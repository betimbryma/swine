package io.bryma.betim.swine.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.DockerCmdExecFactory;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DockerConfiguration {

    @Bean
    public DockerClient getDockerClient(){
        DockerClientConfig config = DefaultDockerClientConfig
                .createDefaultConfigBuilder().build();

        return DockerClientBuilder.getInstance().build();
    }

    public static void main(String[] args){
        DockerClientConfig config = DefaultDockerClientConfig
                .createDefaultConfigBuilder()
                .withDockerHost("tcp://hub.docker.com:80")
                .withRegistryUsername("smartsocietyswine")
                .withRegistryPassword("SmartSocietyReviewer")
                .build();

        DockerCmdExecFactory dockerCmdExecFactory = new JerseyDockerCmdExecFactory()
                .withReadTimeout(1000)
                .withConnectTimeout(1000)
                .withMaxTotalConnections(1000)
                .withMaxPerRouteConnections(10);

        /*DockerClient dockerClient = DockerClientBuilder
                .getInstance(config).withDockerCmdExecFactory(dockerCmdExecFactory)
                .build();
        dockerClient. */
    }
}
