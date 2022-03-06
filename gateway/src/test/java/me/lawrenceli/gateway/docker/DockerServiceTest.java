package me.lawrenceli.gateway.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.RemoveContainerCmd;
import com.github.dockerjava.api.command.StartContainerCmd;
import com.github.dockerjava.api.command.StopContainerCmd;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
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

    @Test
    void testRunContainer() {
        StartContainerCmd any = spy(StartContainerCmd.class);
        when(dockerClient.startContainerCmd("test")).thenReturn(any);
        doNothing().when(any).exec();
        dockerService.runContainer("test");
        verify(dockerClient).startContainerCmd("test");

    }

    @Test
    void testStopContainer() {
        StopContainerCmd any = spy(StopContainerCmd.class);
        when(dockerClient.stopContainerCmd("test")).thenReturn(any);
        doNothing().when(any).exec();
        dockerService.stopContainer("test");
        verify(dockerClient).stopContainerCmd("test");
    }


    @Test
    void testRemoveContainer() {
        RemoveContainerCmd any = spy(RemoveContainerCmd.class);
        when(dockerClient.removeContainerCmd("test")).thenReturn(any);
        doNothing().when(any).exec();
        dockerService.removeContainer("test");
        verify(dockerClient).removeContainerCmd("test");
    }

}