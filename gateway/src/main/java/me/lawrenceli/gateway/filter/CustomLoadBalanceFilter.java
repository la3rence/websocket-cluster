package me.lawrenceli.gateway.filter;

import me.lawrenceli.contant.GlobalConstant;
import me.lawrenceli.gateway.config.WebSocketProperties;
import me.lawrenceli.gateway.server.ServiceNode;
import me.lawrenceli.hashring.ConsistentHashRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.config.LoadBalancerProperties;
import org.springframework.cloud.gateway.filter.LoadBalancerClientFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.server.PathContainer;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;
import java.util.List;

/**
 * 自定义负载均衡策略
 *
 * @author lawrence
 * @since 2021/3/24
 */
public class CustomLoadBalanceFilter extends LoadBalancerClientFilter implements BeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CustomLoadBalanceFilter.class);

    final ConsistentHashRouter<ServiceNode> consistentHashRouter;
    final WebSocketProperties webSocketProperties;
    final DiscoveryClient discoveryClient;

    public CustomLoadBalanceFilter(LoadBalancerClient loadBalancer, LoadBalancerProperties properties, ConsistentHashRouter<ServiceNode> consistentHashRouter, WebSocketProperties webSocketProperties, DiscoveryClient discoveryClient) {
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
            String userId = null;
            if (originalUrl.getPath().startsWith(GlobalConstant.WEBSOCKET_ENDPOINT_PATH)) {
                // ws: "lb://websocket-server/connect/1" 获取这里面的最后一个路径参数 userId: 1
                List<PathContainer.Element> elements = exchange.getRequest().getPath().elements();
                PathContainer.Element lastElement = elements.get(elements.size() - 1);
                userId = lastElement.value();
                logger.debug("【网关负载均衡过滤器】WebSocket 获取到 userId: {}", userId);
            } else {
                // 前提：websocket http 服务 userId 放在 query 中
                // rest: "lb://websocket-server/send?userId=1&message=text"
                MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
                List<String> userIds = queryParams.get(GlobalConstant.KEY_TO_BE_HASHED);
                if (null != userIds && !userIds.isEmpty()) {
                    userId = userIds.get(0);
                    logger.debug("【网关负载均衡过滤器】HTTP 获取到 userId: {}", userId);
                }
            }
            if (null != userId) {
                // 请求参数中有 userId，需要经过哈希环的路由
                ServiceNode serviceNode = consistentHashRouter.routeNode(userId);
                if (null != serviceNode) {
                    // 获取当前注册中心的实例
                    List<ServiceInstance> instances = discoveryClient.getInstances(instancesId);
                    for (ServiceInstance instance : instances) {
                        // 如果 userId 映射后的真实节点的 IP 与某个实例 IP 一致，就转发
                        if (instance.getHost().equals(serviceNode.getKey())) {
                            logger.debug("当前客户端[{}]匹配到真实节点 {}", userId, serviceNode.getKey());
                            return instance;
                        }
                    }
                }
            }
        }
        return super.choose(exchange);
    }
}
