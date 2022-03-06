package me.lawrenceli.websocket.server;

import com.sun.security.auth.UserPrincipal;
import me.lawrenceli.model.WebSocketMessage;
import org.apache.tomcat.websocket.WsRemoteEndpointImplClient;
import org.apache.tomcat.websocket.WsSession;
import org.apache.tomcat.websocket.WsWebSocketContainer;
import org.apache.tomcat.websocket.pojo.PojoEndpointServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.server.standard.ServerEndpointRegistration;

import javax.websocket.DeploymentException;
import javax.websocket.Extension;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ContextConfiguration(classes = {WebSocketEndpoint.class})
@ExtendWith(SpringExtension.class)
class WebSocketEndpointTest {
    @Autowired
    private WebSocketEndpoint webSocketEndpoint;

    @Test
    void testSendMessageBySession() throws IOException, DeploymentException {
        WsRemoteEndpointImplClient wsRemoteEndpointImplClient = mock(WsRemoteEndpointImplClient.class);
        doNothing().when(wsRemoteEndpointImplClient).sendString(any());
        doNothing().when(wsRemoteEndpointImplClient).setSendTimeout(anyLong());
        PojoEndpointServer localEndpoint = new PojoEndpointServer();
        WsWebSocketContainer wsWebSocketContainer = new WsWebSocketContainer();
        URI requestUri = Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri();
        HashMap<String, List<String>> requestParameterMap = new HashMap<>();
        UserPrincipal userPrincipal = new UserPrincipal("userPrincipal");
        ArrayList<Extension> negotiatedExtensions = new ArrayList<>();
        HashMap<String, String> pathParameters = new HashMap<>();
        assertTrue(
                this.webSocketEndpoint.sendMessageBySession(
                        new WsSession(localEndpoint, wsRemoteEndpointImplClient, wsWebSocketContainer, requestUri,
                                requestParameterMap, "Query String", userPrincipal, "42", negotiatedExtensions, "Sub Protocol",
                                pathParameters, true, new ServerEndpointRegistration("Path", new PojoEndpointServer())),
                        "Not all who wander are lost"));
        verify(wsRemoteEndpointImplClient).sendString(any());
        verify(wsRemoteEndpointImplClient).setSendTimeout(anyLong());
    }

    @Test
    void testSendMessageBySession2() {
        assertFalse(this.webSocketEndpoint.sendMessageBySession(null, "Not all who wander are lost"));
    }

    @Test
    void testSendMessageBySession3() throws IOException, DeploymentException {
        WsRemoteEndpointImplClient wsRemoteEndpointImplClient = mock(WsRemoteEndpointImplClient.class);
        doThrow(new IOException("foo")).when(wsRemoteEndpointImplClient).sendString(any());
        doNothing().when(wsRemoteEndpointImplClient).setSendTimeout(anyLong());
        PojoEndpointServer localEndpoint = new PojoEndpointServer();
        WsWebSocketContainer wsWebSocketContainer = new WsWebSocketContainer();
        URI requestUri = Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri();
        HashMap<String, List<String>> requestParameterMap = new HashMap<>();
        UserPrincipal userPrincipal = new UserPrincipal("userPrincipal");
        ArrayList<Extension> negotiatedExtensions = new ArrayList<>();
        HashMap<String, String> pathParameters = new HashMap<>();
        assertFalse(
                this.webSocketEndpoint.sendMessageBySession(
                        new WsSession(localEndpoint, wsRemoteEndpointImplClient, wsWebSocketContainer, requestUri,
                                requestParameterMap, "Query String", userPrincipal, "42", negotiatedExtensions, "Sub Protocol",
                                pathParameters, true, new ServerEndpointRegistration("Path", new PojoEndpointServer())),
                        "Not all who wander are lost"));
        verify(wsRemoteEndpointImplClient).sendString(any());
        verify(wsRemoteEndpointImplClient).setSendTimeout(anyLong());
    }

    @Test
    void testSendMessageToUser() {
        WebSocketMessage webSocketMessage = new WebSocketMessage();
        webSocketMessage.setContent("Not all who wander are lost");
        webSocketMessage.setServerIp("Server Ip");
        webSocketMessage.setServerUserCount(3);
        LocalDateTime atStartOfDayResult = LocalDate.of(1970, 1, 1).atStartOfDay();
        webSocketMessage.setTimestamp(Date.from(atStartOfDayResult.atZone(ZoneId.of("UTC")).toInstant()));
        webSocketMessage.setType(1);
        assertFalse(this.webSocketEndpoint.sendMessageToUser("42", webSocketMessage));
    }

    @Test
    void testSendMessageToUser2() {
        WebSocketMessage webSocketMessage = new WebSocketMessage();
        webSocketMessage.setContent("Not all who wander are lost");
        webSocketMessage.setServerIp("Server Ip");
        webSocketMessage.setServerUserCount(3);
        LocalDateTime atStartOfDayResult = LocalDate.of(1970, 1, 1).atStartOfDay();
        webSocketMessage.setTimestamp(Date.from(atStartOfDayResult.atZone(ZoneId.of("UTC")).toInstant()));
        webSocketMessage.setType(1);
        assertFalse(this.webSocketEndpoint.sendMessageToUser("向 {} - session {} 发送消息: {}", webSocketMessage));
    }

}

