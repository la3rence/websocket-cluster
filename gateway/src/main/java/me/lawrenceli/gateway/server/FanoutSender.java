package me.lawrenceli.gateway.server;

import me.lawrenceli.contant.GlobalConstant;
import me.lawrenceli.utils.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author lawrence
 * @since 2021/3/24
 */
@Component
public class FanoutSender {

    private static final Logger log = LoggerFactory.getLogger(FanoutSender.class);

    private final RabbitTemplate rabbitTemplate;

    public FanoutSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(List<String> clientsToReset) {
        log.info("开始向所有 WebSocket 实例发送广播: [{}]", JSON.toJSONString(clientsToReset));
        rabbitTemplate.convertAndSend(GlobalConstant.FANOUT_EXCHANGE_NAME, "", clientsToReset);
    }
}
