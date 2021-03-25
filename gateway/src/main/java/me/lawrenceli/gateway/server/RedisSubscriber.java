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
 * 使用 redis pub/sub 订阅消息
 *
 * @author lawrence
 * @see RedisConfig
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
     * Redis 订阅者消费
     * 默认方法名 handleMessage, 似乎仅限 String 参数
     */
    @SuppressWarnings("unused")
    public void handleMessage(String webSocketMessageJSON) throws InterruptedException {
        WebSocketMessage webSocketMessage = JSON.parseJSON(webSocketMessageJSON, WebSocketMessage.class);
        logger.info("【Redis 订阅】网关收到消息: {}", webSocketMessage);
        String upOrDown = webSocketMessage.getContent();
        // 实例的标识：IP
        String serverIp = webSocketMessage.getServerIp();
        // 打印看看原先的环：
        logger.info("该实例上线之前的 Hash 环: {}, 稍后将更新...", JSON.toJSONString(consistentHashRouter.getRing()));
        // 网关此时通过此订阅内容，知道了某个 WebSocket 实例发生了变动
        if (GlobalConstant.SERVER_UP_MESSAGE.equals(upOrDown)) {
            // 实例上线, 但 Nacos 可能尚未发现服务，此处再等 Nacos 获取到最新服务列表
            Thread.sleep(6000);
            // 一个服务上线了，应当告知原本hash到其他节点但现在路由到此节点的所有客户端断开连接
            // 首先，确定是哪些客户端，筛选出一个 List<String> userIdClientsToReset
            // 应该遍历全部 userId，计算 hash！筛选出前后匹配到不同真实节点的 userIds
            // 因此每次 WebSocket 有新连接的时候都有必要将 userId(+hash) 保存在 redis 中，然后这里取过来。
            Map<Object, Object> userIdAndHashInRedis = redisTemplate.opsForHash().entries(GlobalConstant.KEY_TO_BE_HASHED);
            logger.info("Redis 中 userId hash : {}", JSON.toJSONString(userIdAndHashInRedis));
            Map<String, ServiceNode> oldUserAndServer = new ConcurrentHashMap<>();
            for (Object userIdObj : userIdAndHashInRedis.keySet()) {
                String userId = (String) userIdObj;
                Long oldHashObj = Long.valueOf(userIdAndHashInRedis.get(userId).toString());
                ServiceNode oldServiceNode = consistentHashRouter.routeNode(userId);
                logger.debug("【遍历】当前客户端 [{}] 的旧节点 [{}]", userId, oldServiceNode);
                oldUserAndServer.put(userId, oldServiceNode);
            }
            // 向 Hash 环添加 node
            ServiceNode serviceNode = new ServiceNode(serverIp);
            consistentHashRouter.addNode(serviceNode, GlobalConstant.VIRTUAL_COUNT);
            // 添加了 node 之后，会发现有部分 userId - serviceNode 映射发生变动
            List<String> userIdClientsToReset = new ArrayList<>();
            for (String userId : oldUserAndServer.keySet()) {
                ServiceNode newServiceNode = consistentHashRouter.routeNode(userId);
                logger.debug("【遍历】当前客户端 [{}] 的新节点 [{}]", userId, newServiceNode);
                // 新旧可能一样，可能不一样, 把前后不一样的跳出来
                if (!newServiceNode.getKey().equals(oldUserAndServer.get(userId).getKey())) {
                    // 找到了发生了变动的 userId
                    userIdClientsToReset.add(userId);
                    logger.info("【哈希环更新】客户端在哈希环的映射发生了变动: [{}]: [{}] -> [{}]...", userId, oldUserAndServer.get(userId), newServiceNode);
                }
            }
            // 通知部分客户端断开连接, 可以发一个全局广播让客户端断开，也可以服务端主动断开，其实就是这些客户端都要自动连接上新的实例
            // MQ Fanout 全局通知广播, 还是得用
            fanoutSender.send(userIdClientsToReset);
        }
        if (GlobalConstant.SERVER_DOWN_MESSAGE.equals(upOrDown)) {
            // 实例下线, 服务端已经立刻主动断连，网关也要移除掉对应节点
            ServiceNode serviceNode = new ServiceNode(serverIp);
            consistentHashRouter.removeNode(serviceNode);
        }
        SortedMap<Long, VirtualNode<ServiceNode>> ring = consistentHashRouter.getRing();
        // 将最新的 ring 放到 redis
        for (Long hash : ring.keySet()) {
            redisTemplate.opsForHash().put(GlobalConstant.HASH_RING_REDIS, hash, ring.get(hash));
        }
        logger.info("该实例上线之后的 Hash 环: {}", JSON.toJSONString(consistentHashRouter.getRing()));

    }
}
