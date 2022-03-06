package me.lawrenceli.websocket.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ContextConfiguration(classes = {ServerUpEventHandler.class})
@ExtendWith(SpringExtension.class)
class ServerUpEventHandlerTest {
    @Autowired
    private ServerUpEventHandler serverUpEventHandler;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void testOnApplicationEvent() {
        doNothing().when(this.stringRedisTemplate).convertAndSend((String) any(), (Object) any());
        SpringApplication application = new SpringApplication(Object.class);
        this.serverUpEventHandler.onApplicationEvent(new ApplicationReadyEvent(application, new String[]{"Args"},
                new AnnotationConfigReactiveWebApplicationContext()));
        verify(this.stringRedisTemplate).convertAndSend((String) any(), (Object) any());
    }
}

