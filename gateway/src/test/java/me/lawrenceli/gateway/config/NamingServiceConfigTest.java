package me.lawrenceli.gateway.config;

import com.alibaba.nacos.client.naming.NacosNamingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author lawrence
 * @since 2021/8/15
 */
class NamingServiceConfigTest {

    private NamingServiceConfig namingServiceConfig;

    @BeforeEach
    void setUp() {
        WebSocketProperties webSocketProperties = new WebSocketProperties();
        webSocketProperties.setNacosServerAddress("127.0.0.1");
        webSocketProperties.setNacosNamespace("test");
        namingServiceConfig = new NamingServiceConfig(webSocketProperties);
    }

    @Test
    void getNamingService() {
        NacosNamingService namingService = namingServiceConfig.getNamingService();
        assertNotNull(namingService);
    }
}