package me.lawrenceli.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lawrence
 * @since 2021/3/23
 */
public class JSON {

    private static final Logger logger = LoggerFactory.getLogger(JSON.class);

    public static <T> String toJSONString(T object) {
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        try {
            json = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("将对象 {} 序列化为 JSON 失败", object);
        }
        return json;
    }

    public static <T> T parseJSON(String json, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        T t = null;
        try {
            t = mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            logger.error("将字符 {} 反序列化为对象失败", json);
        }
        return t;
    }

    /**
     * 工具类私有构造
     */
    private JSON() {
    }
}
