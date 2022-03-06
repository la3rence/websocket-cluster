package me.lawrenceli.gateway.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ServiceNodeTest {
    @Test
    void testConstructor() {
        assertEquals("127.0.0.1", (new ServiceNode("127.0.0.1")).getKey());
    }

    @Test
    void testEquals() {
        assertNotEquals(null, new ServiceNode("127.0.0.1"));
    }

    @Test
    void testEquals2() {
        ServiceNode serviceNode = new ServiceNode("127.0.0.1");
        assertEquals(serviceNode, serviceNode);
        int expectedHashCodeResult = serviceNode.hashCode();
        assertEquals(expectedHashCodeResult, serviceNode.hashCode());
    }

    @Test
    void testEquals3() {
        ServiceNode serviceNode = new ServiceNode("127.0.0.1");
        ServiceNode serviceNode1 = new ServiceNode("127.0.0.1");
        assertEquals(serviceNode, serviceNode1);
        int expectedHashCodeResult = serviceNode.hashCode();
        assertEquals(expectedHashCodeResult, serviceNode1.hashCode());
    }

    @Test
    void testEquals4() {
        ServiceNode serviceNode = new ServiceNode("Ip");
        assertNotEquals(serviceNode, new ServiceNode("127.0.0.1"));
    }
}

