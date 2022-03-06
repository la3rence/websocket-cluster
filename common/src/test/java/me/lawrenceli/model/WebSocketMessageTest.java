package me.lawrenceli.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebSocketMessageTest {

    @Test
    void testToUserOrServerMessage() {
        WebSocketMessage actualToUserOrServerMessageResult = WebSocketMessage.toUserOrServerMessage(MessageType.FOR_USER,
                "Not all who wander are lost", 3);
        assertEquals("Not all who wander are lost", actualToUserOrServerMessageResult.getContent());
        assertEquals(1, actualToUserOrServerMessageResult.getType().intValue());
        assertEquals(3, actualToUserOrServerMessageResult.getServerUserCount().intValue());
        assertTrue(WebSocketMessageTest.ipIsInner(actualToUserOrServerMessageResult.getServerIp()));
    }

    @Test
    void testToUserOrServerMessage2() {
        WebSocketMessage actualToUserOrServerMessageResult = WebSocketMessage.toUserOrServerMessage(MessageType.FOR_SERVER,
                "Not all who wander are lost", 3);
        assertEquals("Not all who wander are lost", actualToUserOrServerMessageResult.getContent());
        assertEquals(2, actualToUserOrServerMessageResult.getType().intValue());
        assertEquals(3, actualToUserOrServerMessageResult.getServerUserCount().intValue());
        assertTrue(WebSocketMessageTest.ipIsInner(actualToUserOrServerMessageResult.getServerIp()));
    }

    /**
     * 私有 IP
     * A 类  10.0.0.0-10.255.255.255
     * B 类  172.16.0.0-172.31.255.255
     * C 类  192.168.0.0-192.168.255.255
     * 127 这个网段是环回地址 localhost
     */
    static List<Pattern> ipFilterRegexList = new ArrayList<>();

    static {
        Set<String> ipFilter = new HashSet<>();
        // A 类
        ipFilter.add("^10\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])"
                + "\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])" + "\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])$");
        // B 类
        ipFilter.add("^172\\.(1[6789]|2[0-9]|3[01])\\" + ".(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])\\"
                + ".(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])$");
        // C 类
        ipFilter.add("^192\\.168\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])\\"
                + ".(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])$");
        ipFilter.add("127.0.0.1");
        ipFilter.add("0.0.0.0");
        ipFilter.add("localhost");
        for (String tmp : ipFilter) {
            ipFilterRegexList.add(Pattern.compile(tmp));
        }
    }

    private static boolean ipIsInner(String ip) {
        boolean isInnerIp = false;
        for (Pattern tmp : ipFilterRegexList) {
            Matcher matcher = tmp.matcher(ip);
            if (matcher.find()) {
                isInnerIp = true;
                break;
            }
        }
        return isInnerIp;
    }
}

