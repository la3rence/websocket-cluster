package me.lawrenceli.gateway.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.command.ListContainersCmdImpl;
import com.google.common.collect.Lists;
import me.lawrenceli.gateway.config.WebSocketProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * @author lawrence
 * @since 2021/8/15
 */
class DockerServiceTest {

    static WebSocketProperties webSocketProperties;

    static {
        webSocketProperties = new WebSocketProperties();
        WebSocketProperties.Docker docker = new WebSocketProperties.Docker();
        docker.setHost("tcp://127.0.0.1:6666");
        webSocketProperties.setDocker(docker);
    }

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private DockerClient dockerClient;

    @InjectMocks
    private DockerService dockerService = new DockerService(webSocketProperties);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void ps() {
        String name = "test";
        List<Container> containers = new ArrayList<>();
        containers.add(new Container());
        when(dockerClient.listContainersCmd().withNameFilter(Lists.newArrayList(name)))
                .thenReturn(new ListContainersCmdImpl(command -> containers));
        List<Container> ps = dockerService.ps(name);
        assertNotNull(ps);
        assertEquals(1, ps.size());
    }
}