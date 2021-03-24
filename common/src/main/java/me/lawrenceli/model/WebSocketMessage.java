package me.lawrenceli.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * @author lawrence
 * @since 2021/3/23
 */
public class WebSocketMessage implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketMessage.class);
    private static final long serialVersionUID = -6637831845278182827L;

    public static WebSocketMessage toUserOrServerMessage(final MessageType type, final String content, final Integer connectSize) {
        WebSocketMessage webSocketMessage = new WebSocketMessage()
                .setType(type.code()).setContent(content)
                .setServerUserCount(connectSize)
                .setTimestamp(new Date());
        try {
            webSocketMessage = webSocketMessage.setServerIp(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            String unknownIp = "0.0.0.0";
            logger.error("获取实例 IP 失败，将被赋值为 {}: {}", unknownIp, e.getMessage());
            webSocketMessage.setServerIp(unknownIp);
        }
        return webSocketMessage;
    }

    // 消息类型
    private Integer type;

    // 消息内容
    private String content;

    // 客户端所连接的服务端的连接数量，仅展示用
    private Integer serverUserCount;

    // 客户端所连接的服务端的局域网 IP，或唯一标识
    private String serverIp;

    // 时间戳
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timestamp;

    public Integer getType() {
        return type;
    }

    public WebSocketMessage setType(Integer type) {
        this.type = type;
        return this;
    }

    public String getContent() {
        return content;
    }

    public WebSocketMessage setContent(String content) {
        this.content = content;
        return this;
    }

    public Integer getServerUserCount() {
        return serverUserCount;
    }

    public WebSocketMessage setServerUserCount(Integer serverUserCount) {
        this.serverUserCount = serverUserCount;
        return this;
    }

    public String getServerIp() {
        return serverIp;
    }

    public WebSocketMessage setServerIp(String serverIp) {
        this.serverIp = serverIp;
        return this;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public WebSocketMessage setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    @Override
    public String toString() {
        return "WebSocketMessage{" +
                "type=" + type +
                ", content='" + content + '\'' +
                ", serverUserCount=" + serverUserCount +
                ", serverIp='" + serverIp + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
