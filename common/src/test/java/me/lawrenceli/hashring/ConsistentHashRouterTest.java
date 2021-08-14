package me.lawrenceli.hashring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import static me.lawrenceli.constant.GlobalConstant.VIRTUAL_COUNT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author lawrence
 * @since 2021/8/14
 */
class ConsistentHashRouterTest {

    private ConsistentHashRouter<Node> consistentHashRouter;

    @BeforeEach
    void setUp() {
        List<Node> nodes = new ArrayList<>();
        nodes.add(() -> "1");
        consistentHashRouter = new ConsistentHashRouter<>(nodes, VIRTUAL_COUNT);
    }

    @Test
    void addNode() {
        consistentHashRouter.addNode(() -> "2", VIRTUAL_COUNT);
        assertSame(VIRTUAL_COUNT, consistentHashRouter.getVirtualNodeCountOf(() -> "2"));
    }

    @Test
    void getRing() {
        SortedMap<Long, VirtualNode<Node>> ring = consistentHashRouter.getRing();
        assertEquals(VIRTUAL_COUNT, ring.size());
    }

    @Test
    void routeNode() {
        Node routeNode = consistentHashRouter.routeNode("1");
        assertEquals("1", routeNode.getKey());
    }

    @Test
    void removeNode() {
        consistentHashRouter.removeNode(() -> "1");
        assertEquals(0, consistentHashRouter.getRing().size());
    }

    @Test
    void getVirtualNodeCountOf() {
        Integer virtualNodeCount = consistentHashRouter.getVirtualNodeCountOf(() -> "1");
        assertEquals(VIRTUAL_COUNT, virtualNodeCount);
    }
}