package me.lawrenceli.websocket.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ContextConfiguration(classes = {ServerDownEventHandler.class})
@ExtendWith(SpringExtension.class)
class ServerDownEventHandlerTest {
    @Autowired
    private ServerDownEventHandler serverDownEventHandler;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void testOnApplicationEvent() {
        doNothing().when(this.stringRedisTemplate).convertAndSend(any(), any());
        this.serverDownEventHandler
                .onApplicationEvent(new ContextClosedEvent(new AnnotationConfigReactiveWebApplicationContext()));
        verify(this.stringRedisTemplate).convertAndSend(any(), any());
    }
}

