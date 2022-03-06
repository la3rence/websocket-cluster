package me.lawrenceli.hashring;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VirtualNodeTest {
    @Test
    void testConstructor() {
        VirtualNode<Node> actualVirtualNode = new VirtualNode<>(mock(Node.class), 1);

        Node expectedPhysicalNode = actualVirtualNode.physicalNode;
        assertSame(expectedPhysicalNode, actualVirtualNode.getPhysicalNode());
        assertEquals(1, actualVirtualNode.replicaIndex.intValue());
    }

    @Test
    void testGetKey() {
        Node node = mock(Node.class);
        when(node.getKey()).thenReturn("Key");
        assertEquals("Key-1", (new VirtualNode<>(node, 1)).getKey());
        verify(node).getKey();
    }

    @Test
    void testIsVirtualOf() {
        Node node = mock(Node.class);
        when(node.getKey()).thenReturn("Key");
        VirtualNode<Node> virtualNode = new VirtualNode<>(node, 1);
        Node node1 = mock(Node.class);
        when(node1.getKey()).thenReturn("Key");
        assertTrue(virtualNode.isVirtualOf(node1));
        verify(node).getKey();
        verify(node1).getKey();
    }

    @Test
    void testIsVirtualOf2() {
        Node node = mock(Node.class);
        when(node.getKey()).thenReturn("foo");
        VirtualNode<Node> virtualNode = new VirtualNode<>(node, 1);
        Node node1 = mock(Node.class);
        when(node1.getKey()).thenReturn("Key");
        assertFalse(virtualNode.isVirtualOf(node1));
        verify(node).getKey();
        verify(node1).getKey();
    }
}

