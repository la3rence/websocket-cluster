package me.lawrenceli.hashring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * T: 真实节点泛型
 *
 * @author lawrence
 * @since 2021/3/23
 */
public class ConsistentHashRouter<T extends Node> {

    private static final Logger logger = LoggerFactory.getLogger(ConsistentHashRouter.class);

    private final HashAlgorithm hashAlgorithm;

    /**
     * 哈希环本体
     * K: 虚拟节点 key 的哈希， V: 虚拟节点
     */
    private final SortedMap<Long, VirtualNode<T>> ring = new TreeMap<>();

    public SortedMap<Long, VirtualNode<T>> getRing() {
        return ring;
    }

    public ConsistentHashRouter(Collection<T> physicalNodes, Integer virtualNodeCount) {
        this(physicalNodes, virtualNodeCount, new MD5Hash());
    }

    public ConsistentHashRouter(Collection<T> physicalNodes, Integer virtualNodeCount, HashAlgorithm hashAlgorithmImpl) {
        this.hashAlgorithm = hashAlgorithmImpl;
        // 遍历真实节点：
        for (T physicalNode : physicalNodes) {
            // 给每个真实节点添加虚拟节点
            addNode(physicalNode, virtualNodeCount);
        }
    }

    /**
     * 核心：根据提供的 业务 key 路由到对应的真实节点
     *
     * @param businessKey 业务key，比如 userID，redis 中的 key 等等分散在不同服务的标识
     * @return 真实节点
     */
    public T routeNode(String businessKey) {
        if (ring.isEmpty()) {
            logger.debug("哈希环为空");
            return null;
        }
        Long hashOfBusinessKey = this.hashAlgorithm.hash(businessKey);
        // 截取哈希环中比当前业务值哈希大的部分环 map
        SortedMap<Long, VirtualNode<T>> biggerTailMap = ring.tailMap(hashOfBusinessKey);
        // 获取路由到的虚拟节点的 hash
        Long nodeHash;
        if (biggerTailMap.isEmpty()) {
            // 没有，回到整个哈希环的环首
            nodeHash = ring.firstKey();
        } else {
            // 存在，则为被截取后的 tailMap 的首个节点 key
            nodeHash = biggerTailMap.firstKey();
        }
        VirtualNode<T> virtualNode = ring.get(nodeHash);
        return virtualNode.getPhysicalNode();
    }

    /**
     * 新增节点
     *
     * @param physicalNode     单个真实节点
     * @param virtualNodeCount 虚拟节点数量（需要限制为自然数）
     */
    public void addNode(T physicalNode, Integer virtualNodeCount) {
        logger.info("【上线】哈希环新增一个真实节点: {}, 虚拟副本节点数量 {}", physicalNode, virtualNodeCount);
        // 先获取当前真实节点的虚拟节点数量
        Integer virtualNodeCountExistBefore = getVirtualNodeCountOf(physicalNode);
        for (int i = 0; i < virtualNodeCount; i++) {
            VirtualNode<T> virtualNode = new VirtualNode<>(physicalNode, virtualNodeCountExistBefore + i);
            ring.put(this.hashAlgorithm.hash(virtualNode.getKey()), virtualNode);
        }
    }

    /**
     * 下线一个真实节点
     *
     * @param physicalNode 真实节点
     */
    public void removeNode(T physicalNode) {
        logger.info("【下线】移除一个真实节点: {}", physicalNode);
        // 实现注意遍历删除可能存在的并发修改异常
        Iterator<Long> iterator = ring.keySet().iterator();
        while (iterator.hasNext()) {
            Long nodeHashKey = iterator.next();
            VirtualNode<T> virtualNode = ring.get(nodeHashKey);
            if (virtualNode.isVirtualOf(physicalNode)) {
                logger.info("【下线：遍历】删除哈希环对应的虚拟节点: {}", nodeHashKey);
                iterator.remove();
            }
        }
    }

    /**
     * 获取当前真实节点的虚拟节点的数量
     *
     * @param physicalNode 真实节点
     * @return 虚拟节点数量
     */
    protected Integer getVirtualNodeCountOf(T physicalNode) {
        int countVirtualNode = 0;
        for (VirtualNode<T> virtualNode : ring.values()) {
            if (virtualNode.isVirtualOf(physicalNode)) {
                countVirtualNode++;
            }
        }
        return countVirtualNode;
    }

    /**
     * 默认 hash 实现
     */
    public static class MD5Hash implements HashAlgorithm {
        MessageDigest instance;

        public MD5Hash() {
            try {
                instance = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                logger.error("获取 MD5 加密实例失败");
            }
        }

        @Override
        public long hash(String key) {
            instance.reset();
            instance.update(key.getBytes());
            byte[] digest = instance.digest();
            long h = 0;
            for (int i = 0; i < 4; i++) {
                h <<= 8;
                h |= (digest[i]) & 0xFF;
            }
            return h;
        }
    }

}
