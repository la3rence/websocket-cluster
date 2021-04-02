package me.lawrenceli.websocket.config;

import me.lawrenceli.constant.GlobalConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
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
    public AnonymousQueue queueForWebSocket() {
        log.info("创建用于 WebSocket 的匿名队列");
        return new AnonymousQueue();
    }

    /**
     * @param fanoutExchange    交换机
     * @param queueForWebSocket 队列
     * @return Binding
     */
    @Bean
    public Binding bindingSingle(FanoutExchange fanoutExchange, AnonymousQueue queueForWebSocket) {
        log.info("把队列 [{}] 绑定到广播交换器 [{}]", queueForWebSocket.getName(), fanoutExchange.getName());
        return BindingBuilder.bind(queueForWebSocket).to(fanoutExchange);
    }
}
