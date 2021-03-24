package me.lawrenceli.websocket.config;

import me.lawrenceli.contant.GlobalConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lawrence
 * @since 2021/3/24
 */
@Configuration
public class MQConfig {

    private static final Logger log = LoggerFactory.getLogger(MQConfig.class);

    @Bean
    public FanoutExchange fanoutExchange() {
        log.info("创建广播交换机 [{}]", GlobalConstant.FANOUT_EXCHANGE_NAME);
        return new FanoutExchange(GlobalConstant.FANOUT_EXCHANGE_NAME);
    }

    @Bean
    public Queue queueForWebSocket() {
        log.info("创建用于 WebSocket 的队列");
        return new Queue(GlobalConstant.QUEUE_NAME_FOR_RECONNECT);
    }

    /**
     * @param fanoutExchange    交换机
     * @param queueForWebSocket 队列
     * @return Binding
     */
    @Bean
    public Binding bindingSingle(FanoutExchange fanoutExchange, Queue queueForWebSocket) {
        log.info("把队列 [{}] 绑定到广播交换器 [{}]", queueForWebSocket.getName(), fanoutExchange.getName());
        return BindingBuilder.bind(queueForWebSocket).to(fanoutExchange);
    }
}
