package me.lawrenceli.contant;

/**
 * @author lawrence
 * @since 2021/3/23
 */
public class GlobalConstant {

    public static final String REDIS_TOPIC_CHANNEL = "server-change";

    public static final String HASH_RING_REDIS = "ring";

    public static final String SERVER_UP_MESSAGE = "UP";

    public static final String SERVER_DOWN_MESSAGE = "DOWN";

    public static final Integer VIRTUAL_COUNT = 10;

    public static final String KEY_TO_BE_HASHED = "userId";

    public static final String FANOUT_EXCHANGE_NAME = "websocket-cluster-exchange";

    public static final String QUEUE_NAME_FOR_RECONNECT = "reconnect-message";

    public static final String WEBSOCKET_ENDPOINT_PATH = "/connect";

    private GlobalConstant() {
    }

}
