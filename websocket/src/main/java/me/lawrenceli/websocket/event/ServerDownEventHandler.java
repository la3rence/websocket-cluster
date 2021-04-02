package me.lawrenceli.websocket.event;

import me.lawrenceli.constant.GlobalConstant;
import me.lawrenceli.model.MessageType;
import me.lawrenceli.model.WebSocketMessage;
import me.lawrenceli.utils.JSON;
import me.lawrenceli.websocket.server.WebSocketEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 服务下线事件处理
 *
 * @author lawrence
 * @since 2021/3/23
 */
@Component
public class ServerDownEventHandler implements ApplicationListener<ContextClosedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ServerDownEventHandler.class);

    final StringRedisTemplate stringRedisTemplate;

    public ServerDownEventHandler(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        logger.debug("当前 WebSocket 实例 - 准备下线 {}", contextClosedEvent.getApplicationContext().getDisplayName());
        logger.info("Redis 发布服务下线消息，通知网关移除相关节点");
        stringRedisTemplate.convertAndSend(GlobalConstant.REDIS_TOPIC_CHANNEL,
                JSON.toJSONString(WebSocketMessage.toUserOrServerMessage(
                        MessageType.FOR_SERVER, GlobalConstant.SERVER_DOWN_MESSAGE, WebSocketEndpoint.sessionMap.size()))
        );
        logger.info("服务实例开始主动断开所有 WebSocket 连接...");
        WebSocketEndpoint.disconnectAllByServer();
    }
}
