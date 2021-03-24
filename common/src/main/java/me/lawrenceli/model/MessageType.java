package me.lawrenceli.model;

/**
 * WebSocket Message's Type enum
 *
 * @author lawrence
 * @since 2021/3/22
 */
public enum MessageType {

    // 用户向消息，纯业务侧消息类型
    FOR_USER {
        public Integer code() {
            return 1;
        }
    },

    // 服务向消息，前端的服务端信息展示类型
    FOR_SERVER {
        public Integer code() {
            return 2;
        }
    };

    public abstract Integer code();

}
