package me.lawrenceli.websocket.controller;

import me.lawrenceli.model.MessageType;
import me.lawrenceli.model.WebSocketMessage;
import me.lawrenceli.websocket.server.WebSocketEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lawrence
 * @since 2021/3/19
 */
@RestController
@RequestMapping("/")
public class WebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    final WebSocketEndpoint webSocketEndpoint;

    public WebSocketController(WebSocketEndpoint webSocketEndpoint) {
        this.webSocketEndpoint = webSocketEndpoint;
    }

    @GetMapping("/send")
    public ResponseEntity<Boolean> send(@RequestParam String userId, @RequestParam String message) {
        final WebSocketMessage webSocketMessage = WebSocketMessage.toUserOrServerMessage(MessageType.FOR_USER, message, WebSocketEndpoint.sessionMap.size());
        logger.info("向用户 {} 发送消息: {}", userId, webSocketMessage);
        return ResponseEntity.ok(webSocketEndpoint.sendMessageToUser(userId, webSocketMessage));
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> count() {
        logger.info("当前 session 连接: {}", WebSocketEndpoint.sessionMap);
        return ResponseEntity.ok(WebSocketEndpoint.sessionMap.size());
    }

}
