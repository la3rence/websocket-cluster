package me.lawrenceli.gateway.docker;

import com.github.dockerjava.api.model.Container;
import me.lawrenceli.gateway.config.WebSocketProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author lawrence
 * @since 2021/3/19
 */
@RestController
@RequestMapping("/docker")
public class DockerController {

    final DockerService dockerService;
    final WebSocketProperties webSocketProperties;

    public DockerController(DockerService dockerService, WebSocketProperties webSocketProperties) {
        this.dockerService = dockerService;
        this.webSocketProperties = webSocketProperties;
    }

    @GetMapping("/ps")
    public ResponseEntity<List<Container>> ps(@RequestParam(required = false) String containerName) {
        return ResponseEntity.ok(dockerService.ps(containerName));
    }

    @GetMapping("/run")
    public ResponseEntity<String> createAndRun() {
        List<Container> containerListOfWebSocketServices = dockerService.ps(webSocketProperties.getService().getName());
        if (containerListOfWebSocketServices.size() < 5) {
            String imageName = webSocketProperties.getDocker().getImage().getName();
            String id = dockerService.createContainer(imageName).getId();
            dockerService.runContainer(id);
            return ResponseEntity.ok(id);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("WebSocket 实例数量达到上限");
    }

    @GetMapping("/rm")
    public ResponseEntity<String> stopAndRemove(@RequestParam String containerId) {
        dockerService.stopContainer(containerId);
        dockerService.removeContainer(containerId);
        return ResponseEntity.ok(containerId);
    }
}
