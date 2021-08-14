package me.lawrenceli.gateway.filter;

import me.lawrenceli.gateway.config.WebSocketProperties;
import me.lawrenceli.gateway.server.ServiceNode;
import me.lawrenceli.hashring.ConsistentHashRouter;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.config.LoadBalancerProperties;
import org.springframework.cloud.gateway.filter.LoadBalancerClientFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;
import java.util.List;

/**
 * 已废弃的自定义负载均衡过滤器
 * - 由于旧的 LoadBalancerClientFilter 已不推荐使用，此旧实现保留，简单易懂，原理不变。
 *
 * @author lawrence
 * @since 2021/3/24
 * @deprecated 推荐使用响应式网关的新过滤器 {@link CustomReactiveLoadBalanceFilter}
 */
@Deprecated
public class CustomLoadBalanceFilter extends LoadBalancerClientFilter implements BeanPostProcessor { // NOSONAR

    final ConsistentHashRouter<ServiceNode> consistentHashRouter;
    final WebSocketProperties webSocketProperties;
    final DiscoveryClient discoveryClient;

    public CustomLoadBalanceFilter(LoadBalancerClient loadBalancer,
                                   LoadBalancerProperties properties,
                                   ConsistentHashRouter<ServiceNode> consistentHashRouter,
                                   WebSocketProperties webSocketProperties,
                                   DiscoveryClient discoveryClient) {
        super(loadBalancer, properties);
        this.consistentHashRouter = consistentHashRouter;
        this.webSocketProperties = webSocketProperties;
        this.discoveryClient = discoveryClient;
    }

    @Override
    protected ServiceInstance choose(ServerWebExchange exchange) {
        URI originalUrl = (URI) exchange.getAttributes().get(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        String instancesId = originalUrl.getHost();
        if (webSocketProperties.getService().getName().equals(instancesId)) {
            // 获取需要参与哈希的字段，此项目为 userId
            final String userId = WebSocketSessionLoadBalancer.getUserIdFromRequest(exchange);
            if (null != userId) {
                // 请求参数中有 userId，需要经过哈希环的路由
                ServiceNode serviceNode = consistentHashRouter.routeNode(userId);
                if (null != serviceNode) {
                    // 获取当前注册中心的实例
                    List<ServiceInstance> instances = discoveryClient.getInstances(instancesId);
                    for (ServiceInstance instance : instances) {
                        // 如果 userId 映射后的真实节点的 IP 与某个实例 IP 一致，就转发
                        if (instance.getHost().equals(serviceNode.getKey())) {
                            return instance;
                        }
                    }
                }
            }
        }
        return super.choose(exchange);
    }
}
