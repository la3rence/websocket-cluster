package me.lawrenceli.gateway.discovery;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import me.lawrenceli.gateway.config.WebSocketProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务发现的端点
 *
 * @author lawrence
 * @since 2021/4/1
 */
@RestController
@RequestMapping("/discovery")
public class DiscoveryController {

    final NamingService namingService;
    final WebSocketProperties webSocketProperties;

    public DiscoveryController(NamingService namingService,
                               WebSocketProperties webSocketProperties) {
        this.namingService = namingService;
        this.webSocketProperties = webSocketProperties;
    }

    /**
     * 前端以短轮询的方式请求，获取 WebSocket 服务运行状态
     *
     * @return false: 服务正在启动中或未注册到 Nacos，true: 服务正常，可接收请求
     * @throws NacosException Nacos 异常
     */
    @GetMapping("/naming")
    public ResponseEntity<Map<String, Boolean>> getServerStatus() throws NacosException {
        List<Instance> allWebSocketInstances = namingService.getAllInstances(webSocketProperties.getService().getName());
        HashMap<String, Boolean> ipAndStatus = new HashMap<>(4);
        for (Instance instance : allWebSocketInstances) {
            ipAndStatus.put(instance.getIp(), instance.isEnabled() && instance.isHealthy());
        }
        return ResponseEntity.ok(ipAndStatus);
    }
}
