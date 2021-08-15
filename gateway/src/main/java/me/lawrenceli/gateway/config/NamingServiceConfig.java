package me.lawrenceli.gateway.config;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.client.naming.NacosNamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * 该 Bean 并没有使用，而是用的 Docker API 来获取实例信息。
 *
 * @author lawrence
 * @since 2021/3/21
 */
@Configuration
public class NamingServiceConfig {

    private static final Logger logger = LoggerFactory.getLogger(NamingServiceConfig.class);

    private final WebSocketProperties webSocketProperties;

    public NamingServiceConfig(WebSocketProperties webSocketProperties) {
        this.webSocketProperties = webSocketProperties;
    }

    @Bean("namingService")
    public NacosNamingService getNamingService() {
        logger.info("注入 Nacos ({}) 名称服务", webSocketProperties.getNacosServerAddress());
        NacosNamingService namingService = null;
        Properties properties = new Properties();
        properties.put("namespace", webSocketProperties.getNacosNamespace());
        properties.put("serverAddr", webSocketProperties.getNacosServerAddress());
        try {
            namingService = new NacosNamingService(properties);
        } catch (NacosException e) {
            logger.error("NacosNamingService 创建异常: {}", e.toString());
        }
        return namingService;
    }
}