package me.lawrenceli.websocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author lawrence
 * @since 2021/3/19
 */
@SpringBootApplication
@EnableDiscoveryClient
public class WebSocketApp {

    public static void main(String[] args) {
        SpringApplication.run(WebSocketApp.class);
    }


}
