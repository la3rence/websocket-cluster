package me.lawrenceli.gateway.server;

import me.lawrenceli.constant.GlobalConstant;
import me.lawrenceli.hashring.ConsistentHashRouter;
import me.lawrenceli.model.MessageType;
import me.lawrenceli.model.WebSocketMessage;
import me.lawrenceli.utils.JSON;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;

/**
 * @author lawrence
 * @since 2021/8/19
 */
class RedisSubscriberTest {

    @InjectMocks
    RedisSubscriber redisSubscriber;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    RedisTemplate<Object, Object> redisTemplate;

    @Mock
    FanoutSender fanoutSender;

    private ConsistentHashRouter<ServiceNode> consistentHashRouter;

    @BeforeEach
    void setUp() {
        List<ServiceNode> nodes = new ArrayList<>();
        nodes.add(new ServiceNode("192.168.0.0"));
        nodes.add(new ServiceNode("192.168.0.1"));
        consistentHashRouter = new ConsistentHashRouter<>(nodes, GlobalConstant.VIRTUAL_COUNT);
        MockitoAnnotations.initMocks(this);
        redisSubscriber.setConsistentHashRouter(consistentHashRouter);
    }

    @Nested
    @DisplayName("Test handleMessage")
    class HandleMessage {
        @BeforeEach
        void setUp() {
            Mockito.when(redisTemplate.opsForHash().entries(GlobalConstant.KEY_TO_BE_HASHED))
                    .thenReturn(mockUserIdAndHashInRedis());
            Mockito.doNothing().when(fanoutSender).send(anyList());
        }

        @Test
        @DisplayName("Test When Server Up")
        void testHandleUpMessage() throws InterruptedException {
            WebSocketMessage webSocketServerUpMessage = generateServerChangeMessage("UP");
            assertEquals(new ServiceNode("192.168.0.0"), consistentHashRouter.routeNode("100"));
            redisSubscriber.handleMessage(JSON.toJSONString(webSocketServerUpMessage));

            assertEquals(3 * GlobalConstant.VIRTUAL_COUNT, consistentHashRouter.getRing().size());
            assertEquals(new ServiceNode("192.168.0.2"), consistentHashRouter.routeNode("100"));
        }

        @Test
        @DisplayName("Test When Server Down")
        void testHandleDownMessage() throws InterruptedException {
            WebSocketMessage webSocketServerDownMessage = generateServerChangeMessage("DOWN");
            redisSubscriber.handleMessage(JSON.toJSONString(webSocketServerDownMessage));

            assertEquals(2 * GlobalConstant.VIRTUAL_COUNT, consistentHashRouter.getRing().size());
            assertEquals(new ServiceNode("192.168.0.0"), consistentHashRouter.routeNode("100"));
        }
    }


    private Map<Object, Object> mockUserIdAndHashInRedis() {
        ConsistentHashRouter.MD5Hash md5Hash = new ConsistentHashRouter.MD5Hash();
        HashMap<Object, Object> userIdAndHash = new HashMap<>(2);
        userIdAndHash.put("100", md5Hash.hash("100"));
        userIdAndHash.put("200", md5Hash.hash("200"));
        return userIdAndHash;
    }

    private WebSocketMessage generateServerChangeMessage(String upOrDown) {
        return new WebSocketMessage().setType(MessageType.FOR_SERVER.code())
                .setServerUserCount(2)
                .setTimestamp(new Date())
                .setContent(upOrDown).setServerIp("192.168.0.2");
    }

}