package me.lawrenceli.hashring;

import java.io.Serializable;

/**
 * 真实节点的副本：虚拟节点
 *
 * @author lawrence
 * @since 2021/3/23
 */
public class VirtualNode<T extends Node> implements Node, Serializable {

    private static final long serialVersionUID = -1066173071509622053L;

    // 真实节点
    final T physicalNode;

    // 虚拟节点作为真实节点的副本，给它加个 -index 区分
    final Integer replicaIndex;

    public VirtualNode(T physicalNode, Integer replicaIndex) {
        this.physicalNode = physicalNode;
        this.replicaIndex = replicaIndex;
    }

    @Override
    public String getKey() {
        return physicalNode.getKey() + "-" + replicaIndex;
    }

    /**
     * 是否作为某个真实节点的虚拟副本节点
     *
     * @param anyPhysicalNode 任何真实节点
     * @return 是/否
     */
    public boolean isVirtualOf(T anyPhysicalNode) {
        return anyPhysicalNode.getKey().equals(this.physicalNode.getKey());
    }

    /**
     * 获取当前虚拟节点的真实节点
     *
     * @return 真实节点
     */
    public T getPhysicalNode() {
        return this.physicalNode;
    }

}
