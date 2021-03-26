package me.lawrenceli.gateway.server;

import me.lawrenceli.contant.GlobalConstant;
import me.lawrenceli.gateway.config.RedisConfig;
import me.lawrenceli.gateway.config.WebSocketProperties;
import me.lawrenceli.hashring.ConsistentHashRouter;
import me.lawrenceli.hashring.VirtualNode;
import me.lawrenceli.model.WebSocketMessage;
import me.lawrenceli.utils.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 使用 Redis pub/sub 订阅消息
 *
 * @author lawrence
 * @since 2021/3/23
 */
@Component
public class RedisSubscriber {

    private static final Logger logger = LoggerFactory.getLogger(RedisSubscriber.class);

    final DiscoveryClient discoveryClient; // spring cloud native interface
    final WebSocketProperties webSocketProperties;
    final ConsistentHashRouter<ServiceNode> consistentHashRouter;
    final RedisTemplate<Object, Object> redisTemplate;
    final FanoutSender fanoutSender;

    public RedisSubscriber(DiscoveryClient discoveryClient,
                           WebSocketProperties webSocketProperties,
                           ConsistentHashRouter<ServiceNode> consistentHashRouter,
                           RedisTemplate<Object, Object> redisTemplate, FanoutSender fanoutSender) {
        this.discoveryClient = discoveryClient;
        this.webSocketProperties = webSocketProperties;
        this.consistentHashRouter = consistentHashRouter;
        this.redisTemplate = redisTemplate;
        this.fanoutSender = fanoutSender;
    }

    /**
     * Redis 订阅者消费, 默认方法名为 handleMessage,
     *
     * @param webSocketMessageJSON WebSocket 实例上下线事件的消息： WebSocketMessage 对象的序列化字符串
     * @see <a href="https://lawrenceli.me/blog/websocket-cluster">WebSocket 集群方案</a>
     * @see RedisConfig
     */
    @SuppressWarnings("unused")
    public void handleMessage(String webSocketMessageJSON) throws InterruptedException {
        WebSocketMessage webSocketMessage = JSON.parseJSON(webSocketMessageJSON, WebSocketMessage.class);
        logger.info("【Redis 订阅】网关收到 WebSocket 实例变化消息: {}", webSocketMessage);
        String upOrDown = webSocketMessage.getContent();
        // 实例的标识：IP
        String serverIp = webSocketMessage.getServerIp();
        logger.info("【哈希环】该实例上线之前为 {}, 稍后将更新...", JSON.toJSONString(consistentHashRouter.getRing()));
        if (GlobalConstant.SERVER_UP_MESSAGE.equalsIgnoreCase(upOrDown)) {
            // 实例上线, 但 Nacos 可能尚未发现服务，此处再等 Nacos 获取到最新服务列表
            Thread.sleep(5000);
            // 一个服务上线了，应当告知原本哈希到其他节点但现在路由到此节点的所有客户端断开连接
            // 为了确定是哪些客户端需要重连，可以遍历所有 userId 和哈希，筛选出节点添加前后匹配到不同真实节点的所有 userId
            // 因此，每次 WebSocket 有新连接(onOpen)的时候都有必要将 userId(+hash) 保存在 redis 中，然后在节点变动时取出
            Map<Object, Object> userIdAndHashInRedis = redisTemplate.opsForHash().entries(GlobalConstant.KEY_TO_BE_HASHED);
            logger.debug("Redis 中 userId hash : {}", JSON.toJSONString(userIdAndHashInRedis));
            Map<String, ServiceNode> oldUserAndServer = new ConcurrentHashMap<>();
            for (Object userIdObj : userIdAndHashInRedis.keySet()) {
                String userId = (String) userIdObj;
                Long oldHashObj = Long.valueOf(userIdAndHashInRedis.get(userId).toString());
                ServiceNode oldServiceNode = consistentHashRouter.routeNode(userId);
                logger.debug("【遍历】当前客户端 [{}] 的旧节点 [{}]", userId, oldServiceNode);
                // 如果 WebSocket 实例上线之前就有了客户端的连接，重连间隙可能只有几秒，极有可能此时哈希环是空的
                // https://github.com/Lonor/websocket-cluster/issues/2
                if (null != oldServiceNode) {
                    oldUserAndServer.put(userId, oldServiceNode);
                }
            }
            // 向 Hash 环添加 node
            ServiceNode serviceNode = new ServiceNode(serverIp);
            consistentHashRouter.addNode(serviceNode, GlobalConstant.VIRTUAL_COUNT);
            // 添加了 node 之后就可能有部分 userId 路由到的真实服务节点发生变动
            List<String> userIdClientsToReset = new ArrayList<>();
            for (String userId : oldUserAndServer.keySet()) {
                ServiceNode newServiceNode = consistentHashRouter.routeNode(userId);
                logger.debug("【遍历】当前客户端 [{}] 的新节点 [{}]", userId, newServiceNode);
                // 同一 userId 路由到的真实服务节点前后可能会不一样, 把这些 userId 筛选出来
                if (!newServiceNode.getKey().equals(oldUserAndServer.get(userId).getKey())) {
                    userIdClientsToReset.add(userId);
                    logger.info("【哈希环更新】客户端在哈希环的映射服务节点发生了变动: [{}]: [{}] -> [{}]", userId, oldUserAndServer.get(userId), newServiceNode);
                }
            }
            // 通知部分客户端断开连接, 可以发一个全局广播让客户端断开，也可以服务端主动断开，其实就是这些客户端都要自动连接上新的实例
            fanoutSender.send(userIdClientsToReset);
        }
        if (GlobalConstant.SERVER_DOWN_MESSAGE.equalsIgnoreCase(upOrDown)) {
            // 实例下线, 服务端已经立刻主动断连，网关也要移除掉对应节点
            ServiceNode serviceNode = new ServiceNode(serverIp);
            consistentHashRouter.removeNode(serviceNode);
        }
        // 将最新的哈希环放到 Redis
        SortedMap<Long, VirtualNode<ServiceNode>> ring = consistentHashRouter.getRing();
        redisTemplate.opsForHash().putAll(GlobalConstant.HASH_RING_REDIS, ring);
        logger.info("【哈希环】实例上线之后为 {}", JSON.toJSONString(ring));
    }
}
