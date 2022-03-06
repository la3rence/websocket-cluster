package me.lawrenceli.websocket.controller;

import me.lawrenceli.websocket.server.WebSocketEndpoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.ContentResultMatchers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {WebSocketController.class})
@ExtendWith(SpringExtension.class)
class WebSocketControllerTest {
    @Autowired
    private WebSocketController webSocketController;

    @MockBean(name = "websocketEndpoint")
    private WebSocketEndpoint webSocketEndpoint;

    @Test
    void testCount() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/count");
        MockMvcBuilders.standaloneSetup(this.webSocketController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("0"));
    }

    @Test
    void testCount2() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/count", "Uri Vars");
        MockMvcBuilders.standaloneSetup(this.webSocketController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("0"));
    }

    @Test
    void testSend() throws Exception {
        when(this.webSocketEndpoint.sendMessageToUser(any(), any()))
                .thenReturn(true);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/send")
                .param("message", "foo")
                .param("userId", "foo");
        ResultActions resultActions = MockMvcBuilders.standaloneSetup(this.webSocketController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"));
        ContentResultMatchers contentResult = MockMvcResultMatchers.content();
        resultActions.andExpect(contentResult.string(Boolean.TRUE.toString()));
    }
}

