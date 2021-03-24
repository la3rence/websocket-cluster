package me.lawrenceli.gateway.controller;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import me.lawrenceli.gateway.config.WebSocketProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author lawrence
 * @since 2021/3/19
 */
@RestController
@RequestMapping("/gateway")
public class GatewayController {

    final NamingService namingService;
    final WebSocketProperties webSocketProperties;

    public GatewayController(NamingService namingService, WebSocketProperties webSocketProperties) {
        this.namingService = namingService;
        this.webSocketProperties = webSocketProperties;
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/service")
    public ResponseEntity<List<Instance>> getServices() throws NacosException {
        List<Instance> allInstances = namingService.getAllInstances(webSocketProperties.getService().getName());
        return ResponseEntity.ok(allInstances);
    }
}
