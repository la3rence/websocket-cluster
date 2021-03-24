package me.lawrenceli.websocket.server;

import me.lawrenceli.contant.GlobalConstant;
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

    @RabbitListener(queues = GlobalConstant.QUEUE_NAME_FOR_RECONNECT)
    public void receiver(List<String> clientsToReset) {
        log.info("队列接收到了消息: [{}]", clientsToReset.toString());
        WebSocketEndpoint.disconnectSomeByServer(clientsToReset);
    }

}