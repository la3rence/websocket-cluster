package me.lawrenceli.gateway.server;

import me.lawrenceli.hashring.Node;

import java.io.Serializable;

/**
 * 真实节点
 *
 * @author lawrence
 * @since 2021/3/23
 */
public class ServiceNode implements Node, Serializable {

    private static final long serialVersionUID = 5410221835105700427L;

    // 仅用 IP 来作为划分实例的依据
    private final String ip;

    public ServiceNode(String ip) {
        this.ip = ip;
    }

    @Override
    public String getKey() {
        return ip;
    }

    @Override
    public String toString() {
        return getKey();
    }
}
