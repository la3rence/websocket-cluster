package me.lawrenceli.gateway.config;

import me.lawrenceli.constant.GlobalConstant;
import me.lawrenceli.gateway.filter.CustomReactiveLoadBalanceFilter;
import me.lawrenceli.gateway.server.ServiceNode;
import me.lawrenceli.hashring.ConsistentHashRouter;
import me.lawrenceli.hashring.VirtualNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.config.LoadBalancerProperties;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 初始化
 *
 * @author lawrence
 * @since 2021/3/23
 */
@Configuration
public class GatewayHashRingConfig {

    private static final Logger logger = LoggerFactory.getLogger(GatewayHashRingConfig.class);

    final RedisTemplate<Object, Object> redisTemplate;
    final WebSocketProperties webSocketProperties;
    private final LoadBalancerClientFactory clientFactory;

    public GatewayHashRingConfig(RedisTemplate<Object, Object> redisTemplate,
                                 WebSocketProperties webSocketProperties,
                                 LoadBalancerClientFactory clientFactory) {
        this.redisTemplate = redisTemplate;
        this.webSocketProperties = webSocketProperties;
        this.clientFactory = clientFactory;
    }

    /**
     * @param client                 负载均衡客户端
     * @param loadBalancerProperties 负载均衡配置
     * @param consistentHashRouter   {@link #init() init方法}注入，此处未使用构造注入（会产生循环依赖）
     * @param discoveryClient        服务发现客户端
     * @return 注入自定义的 Reactive 过滤器 Bean 对象
     */
    @Bean
    public CustomReactiveLoadBalanceFilter customReactiveLoadBalanceFilter(LoadBalancerClient client,
                                                                           LoadBalancerProperties loadBalancerProperties,
                                                                           ConsistentHashRouter<ServiceNode> consistentHashRouter,
                                                                           DiscoveryClient discoveryClient) {
        logger.debug("初始化 自定义响应式负载均衡器: {}, {}", client, loadBalancerProperties);
        return new CustomReactiveLoadBalanceFilter(clientFactory, loadBalancerProperties,
                consistentHashRouter, discoveryClient, webSocketProperties);
    }

    @Bean
    @SuppressWarnings("unchecked")
    public ConsistentHashRouter<ServiceNode> init() {
        // 先从 Redis 中获取哈希环(网关集群)
        final Map<Object, Object> ring = redisTemplate.opsForHash().entries(GlobalConstant.HASH_RING_REDIS);
        // 获取环中的所有真实节点
        List<ServiceNode> serviceNodes = new ArrayList<>();
        for (Object key : ring.keySet()) {
            Long hashKey = (Long) key;
            VirtualNode<ServiceNode> virtualNode = (VirtualNode<ServiceNode>) ring.get(hashKey);
            ServiceNode physicalNode = virtualNode.getPhysicalNode();
            serviceNodes.add(physicalNode);
        }
        ConsistentHashRouter<ServiceNode> consistentHashRouter = new ConsistentHashRouter<>(serviceNodes, GlobalConstant.VIRTUAL_COUNT);
        logger.debug("初始化 ConsistentHashRouter: {}", consistentHashRouter);
        return consistentHashRouter;
    }

}
