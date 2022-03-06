package me.lawrenceli.gateway.discovery;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.NacosNamingService;
import me.lawrenceli.gateway.config.WebSocketProperties;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DiscoveryControllerTest {
    @Test
    void testGetServerStatus() throws NacosException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R005 Unable to load class.
        //   Class: javax.servlet.ServletContext
        //   Please check that the class is available on your test runtime classpath.
        //   See https://diff.blue/R005 to resolve this issue.

        NacosNamingService nacosNamingService = mock(NacosNamingService.class);
        when(nacosNamingService.getAllInstances(any())).thenReturn(new ArrayList<>());

        WebSocketProperties.Service service = new WebSocketProperties.Service();
        service.setName("42");

        WebSocketProperties webSocketProperties = new WebSocketProperties();
        webSocketProperties.setService(service);
        ResponseEntity<Map<String, Boolean>> actualServerStatus = (new DiscoveryController(nacosNamingService,
                webSocketProperties)).getServerStatus();
        assertTrue(actualServerStatus.hasBody());
        assertEquals(HttpStatus.OK, actualServerStatus.getStatusCode());
        assertTrue(actualServerStatus.getHeaders().isEmpty());
        verify(nacosNamingService).getAllInstances(any());
    }

    @Test
    void testGetServerStatus2() throws NacosException {
        ArrayList<Instance> instanceList = new ArrayList<>();
        instanceList.add(new Instance());
        NacosNamingService nacosNamingService = mock(NacosNamingService.class);
        when(nacosNamingService.getAllInstances(any())).thenReturn(instanceList);

        WebSocketProperties.Service service = new WebSocketProperties.Service();
        service.setName("42");

        WebSocketProperties webSocketProperties = new WebSocketProperties();
        webSocketProperties.setService(service);
        ResponseEntity<Map<String, Boolean>> actualServerStatus = (new DiscoveryController(nacosNamingService,
                webSocketProperties)).getServerStatus();
        assertEquals(1, Objects.requireNonNull(actualServerStatus.getBody()).size());
        assertTrue(actualServerStatus.hasBody());
        assertEquals(HttpStatus.OK, actualServerStatus.getStatusCode());
        assertTrue(actualServerStatus.getHeaders().isEmpty());
        verify(nacosNamingService).getAllInstances(any());
    }

    @Test
    void testGetServerStatus3() throws NacosException {
        Instance instance = mock(Instance.class);
        when(instance.isHealthy()).thenReturn(false);
        when(instance.isEnabled()).thenReturn(true);
        when(instance.getIp()).thenReturn("127.0.0.1");

        ArrayList<Instance> instanceList = new ArrayList<>();
        instanceList.add(instance);
        NacosNamingService nacosNamingService = mock(NacosNamingService.class);
        when(nacosNamingService.getAllInstances(any())).thenReturn(instanceList);

        WebSocketProperties.Service service = new WebSocketProperties.Service();
        service.setName("42");

        WebSocketProperties webSocketProperties = new WebSocketProperties();
        webSocketProperties.setService(service);
        ResponseEntity<Map<String, Boolean>> actualServerStatus = (new DiscoveryController(nacosNamingService,
                webSocketProperties)).getServerStatus();
        assertEquals(1, Objects.requireNonNull(actualServerStatus.getBody()).size());
        assertTrue(actualServerStatus.hasBody());
        assertEquals(HttpStatus.OK, actualServerStatus.getStatusCode());
        assertTrue(actualServerStatus.getHeaders().isEmpty());
        verify(nacosNamingService).getAllInstances(any());
        verify(instance).isEnabled();
        verify(instance).isHealthy();
        verify(instance).getIp();
    }

    @Test
    void testGetServerStatus4() throws NacosException {
        Instance instance = mock(Instance.class);
        when(instance.isHealthy()).thenReturn(true);
        when(instance.isEnabled()).thenReturn(false);
        when(instance.getIp()).thenReturn("127.0.0.1");

        ArrayList<Instance> instanceList = new ArrayList<>();
        instanceList.add(instance);
        NacosNamingService nacosNamingService = mock(NacosNamingService.class);
        when(nacosNamingService.getAllInstances(any())).thenReturn(instanceList);

        WebSocketProperties.Service service = new WebSocketProperties.Service();
        service.setName("42");

        WebSocketProperties webSocketProperties = new WebSocketProperties();
        webSocketProperties.setService(service);
        ResponseEntity<Map<String, Boolean>> actualServerStatus = (new DiscoveryController(nacosNamingService,
                webSocketProperties)).getServerStatus();
        assertEquals(1, Objects.requireNonNull(actualServerStatus.getBody()).size());
        assertTrue(actualServerStatus.hasBody());
        assertEquals(HttpStatus.OK, actualServerStatus.getStatusCode());
        assertTrue(actualServerStatus.getHeaders().isEmpty());
        verify(nacosNamingService).getAllInstances(any());
        verify(instance).isEnabled();
        verify(instance).getIp();
    }
}

