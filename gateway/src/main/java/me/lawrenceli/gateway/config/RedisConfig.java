package me.lawrenceli.gateway.config;

import me.lawrenceli.contant.GlobalConstant;
import me.lawrenceli.gateway.server.RedisSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * Redis pub/sub 配置
 *
 * @author lawrence
 * @since 2021/3/23
 */
@Configuration
public class RedisConfig {

    @Bean
    MessageListenerAdapter listenerAdapter(RedisSubscriber redisSubscriber) {
        // 该构造的第二个参数是 消费方的方法名字符串
        return new MessageListenerAdapter(redisSubscriber);
    }

    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                            MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new PatternTopic(GlobalConstant.REDIS_TOPIC_CHANNEL));
        return container;
    }
}
