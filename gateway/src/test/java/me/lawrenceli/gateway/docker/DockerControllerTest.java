package me.lawrenceli.gateway.docker;

import com.github.dockerjava.api.model.Container;
import me.lawrenceli.gateway.config.WebSocketProperties;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DockerControllerTest {
    @Test
    void testPs() {
        DockerService dockerService = mock(DockerService.class);
        when(dockerService.ps(any())).thenReturn(new ArrayList<>());
        ResponseEntity<List<Container>> actualPsResult = (new DockerController(dockerService, new WebSocketProperties()))
                .ps("Container Name");
        assertTrue(actualPsResult.hasBody());
        assertEquals(HttpStatus.OK, actualPsResult.getStatusCode());
        assertTrue(actualPsResult.getHeaders().isEmpty());
        verify(dockerService).ps(any());
    }

    @Test
    void testStopAndRemove() {
        DockerService dockerService = mock(DockerService.class);
        doNothing().when(dockerService).removeContainer(any());
        doNothing().when(dockerService).stopContainer(any());
        ResponseEntity<String> actualStopAndRemoveResult = (new DockerController(dockerService, new WebSocketProperties()))
                .stopAndRemove("42");
        assertEquals("42", actualStopAndRemoveResult.getBody());
        assertEquals(HttpStatus.OK, actualStopAndRemoveResult.getStatusCode());
        assertTrue(actualStopAndRemoveResult.getHeaders().isEmpty());
        verify(dockerService).removeContainer(any());
        verify(dockerService).stopContainer(any());
    }
}

