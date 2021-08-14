package me.lawrenceli.websocket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author lawrence
 * @since 2021/3/24
 */
@Component
public class FanoutReceiver {

    private static final Logger log = LoggerFactory.getLogger(FanoutReceiver.class);

    @RabbitListener(queues = "#{queueForWebSocket.name}")
    public void receiver(List<String> clientsToReset) {
        log.info("队列接收到了主动断掉服务端 WebSocket 连接的消息: [{}]", clientsToReset);
        WebSocketEndpoint.disconnectSomeByServer(clientsToReset);
    }

}