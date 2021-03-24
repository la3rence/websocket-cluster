package me.lawrenceli.gateway.config;

import me.lawrenceli.contant.GlobalConstant;
import me.lawrenceli.gateway.filter.CustomLoadBalanceFilter;
import me.lawrenceli.gateway.server.ServiceNode;
import me.lawrenceli.hashring.ConsistentHashRouter;
import me.lawrenceli.hashring.VirtualNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.config.LoadBalancerProperties;
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

    public GatewayHashRingConfig(RedisTemplate<Object, Object> redisTemplate, WebSocketProperties webSocketProperties) {
        this.redisTemplate = redisTemplate;
        this.webSocketProperties = webSocketProperties;
    }

    /**
     * 初始化自定义负载均衡过滤器
     *
     * @param client               bean
     * @param properties           bean
     * @param consistentHashRouter {@link #init() init方法}注入，此处未使用构造注入（会产生循环依赖）
     * @return CustomLoadBalanceFilter bean
     * @see CustomLoadBalanceFilter
     */
    @Bean
    public CustomLoadBalanceFilter customLoadBalanceFilter(LoadBalancerClient client,
                                                           LoadBalancerProperties properties,
                                                           ConsistentHashRouter<ServiceNode> consistentHashRouter,
                                                           DiscoveryClient discoveryClient) {
        logger.debug("初始化 CustomLoadBalanceFilter: {}, {}", client, properties);
        return new CustomLoadBalanceFilter(client, properties, consistentHashRouter, webSocketProperties, discoveryClient);
    }

    @Bean
    public ConsistentHashRouter<ServiceNode> init() {
        // 先从 Redis 中获取哈希环
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
        logger.debug("初始化 ConsistentHashRouter: {}", consistentHashRouter.toString());
        return consistentHashRouter;
    }

}
