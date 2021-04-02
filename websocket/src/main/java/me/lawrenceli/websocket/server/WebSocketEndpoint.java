package me.lawrenceli.websocket.server;

import me.lawrenceli.constant.GlobalConstant;
import me.lawrenceli.hashring.ConsistentHashRouter;
import me.lawrenceli.model.MessageType;
import me.lawrenceli.model.WebSocketMessage;
import me.lawrenceli.utils.JSON;
import me.lawrenceli.websocket.spring.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lawrence
 * @since 2021/3/19
 */
@Component("websocketEndpoint")
@ServerEndpoint(GlobalConstant.WEBSOCKET_ENDPOINT_PATH + "/{userId}")
public class WebSocketEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEndpoint.class);

    /**
     * Websocket session 内存
     * key: userId
     * value: websocket session
     */
    public static ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<>();

    // Do not make DI with `@autowired` or constructor in this class with `@ServerEndpoint` annotated.

    /**
     * 服务端发送消息给客户端
     *
     * @param session WebSocket 连接
     * @param message 消息内容
     * @return 是否发送成功
     */
    protected Boolean sendMessageBySession(Session session, String message) {
        if (session != null) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException ignore) {
                logger.error("WebSocket 通信时发生未知的 IO 异常 [message{}, session{}]", message, session);
                return false;
            }
            return true;
        } else {
            logger.debug("当前用户未连接，客户端提示失败");
            return false;
        }
    }

    public Boolean sendMessageToUser(String userId, WebSocketMessage webSocketMessage) {
        Session session = sessionMap.get(userId);
        try {
            logger.debug("向 {} - session {} 发送消息: {}", userId, session.getId(), webSocketMessage);
            return this.sendMessageBySession(session, JSON.toJSONString(webSocketMessage));
        } catch (Exception e) {
            logger.error("服务端发给用户 {} 发送消息异常: {}", userId, e.getMessage());
            return false;
        }
    }

    @OnOpen
    public void onOpen(Session session, @PathParam(value = "userId") String userId) {
        sessionMap.put(userId, session);
        // 将 userId，hash(userId) 保存到全局 redis，是为了有新的实例上线时筛选部分变动的客户端主动重新连接到新的实例上
        RedisTemplate<Object, Object> redisTemplate = (RedisTemplate<Object, Object>) BeanUtils.getBean("redisTemplate");
        redisTemplate.opsForHash().put(GlobalConstant.KEY_TO_BE_HASHED, userId, String.valueOf(new ConsistentHashRouter.MD5Hash().hash(userId)));
        logger.info("客户端用户 {} 连接, 当前实例连接数: [{}] ", userId, sessionMap.size());
        // 发一条消息告诉客户端情况
        WebSocketMessage webSocketMessage = WebSocketMessage.toUserOrServerMessage(MessageType.FOR_SERVER, "已连接", sessionMap.size());
        this.sendMessageToUser(userId, webSocketMessage);
    }

    @OnError
    public void onError(Throwable throwable) {
        logger.error("WebSocket 出现错误: " + throwable.getMessage());
    }

    @OnClose
    public void onClose(@PathParam(value = "userId") String userId) {
        sessionMap.remove(userId);
        RedisTemplate<Object, Object> redisTemplate = (RedisTemplate<Object, Object>) BeanUtils.getBean("redisTemplate");
        redisTemplate.opsForHash().delete(GlobalConstant.KEY_TO_BE_HASHED, userId, new ConsistentHashRouter.MD5Hash().hash(userId));
        logger.info("客户端用户 {} 断开连接, 从内存和 Redis 中移除", userId);
        // 关闭连接时 sessionMap 的 size 即连接数需要立刻更新到前端服务列表的 userCount 中
        // 为了不批量发送，定义一个是否发出去的标记，确保最多发送一次
        boolean sent = false;
        if (sessionMap.size() > 0) {
            // 获取不是当前 session 的其他客户端
            for (String anyClientUserId : sessionMap.keySet()) {
                if (!sent) {
                    // 若未发送，则尝试发送，成功后修改标记
                    logger.debug("通知客户端更新服务连接数");
                    sent = sendMessageToUser(anyClientUserId, WebSocketMessage.toUserOrServerMessage(MessageType.FOR_SERVER, "更新服务列表", sessionMap.size()));
                }
            }
        }
    }

    public static void disconnectAllByServer() {
        for (String userId : sessionMap.keySet()) {
            Session session = sessionMap.get(userId);
            try {
                session.close();
                logger.info("当前服务端已主动断开 {} 的连接", userId);
            } catch (IOException e) {
                logger.error("服务端主动断开 {} 连接发生异常: {}", userId, e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void disconnectSomeByServer(List<String> userIds) {
        for (String userId : userIds) {
            if (sessionMap.containsKey(userId)) {
                Session session = sessionMap.get(userId);
                logger.info("【MQ 通知重连】当前服务端已主动断开 {} 的连接", userId);
                try {
                    session.close();
                } catch (IOException e) {
                    logger.error("服务端主动断开 {} 连接发生异常: {}", userId, e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
