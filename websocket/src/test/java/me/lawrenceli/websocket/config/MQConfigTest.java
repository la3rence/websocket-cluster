package me.lawrenceli.websocket.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.FanoutExchange;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MQConfigTest {
    @Test
    void testFanoutExchange() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R004 No meaningful assertions found.
        //   Diffblue Cover was unable to create an assertion.
        //   Make sure that fields modified by fanoutExchange()
        //   have package-private, protected, or public getters.
        //   See https://diff.blue/R004 to resolve this issue.

        FanoutExchange actualFanoutExchangeResult = (new MQConfig()).fanoutExchange();
        assertTrue(actualFanoutExchangeResult.getArguments().isEmpty());
        assertTrue(actualFanoutExchangeResult.shouldDeclare());
        assertTrue(actualFanoutExchangeResult.isDurable());
        assertFalse(actualFanoutExchangeResult.isAutoDelete());
        assertEquals("websocket-cluster-exchange", actualFanoutExchangeResult.getName());
        assertTrue(actualFanoutExchangeResult.getDeclaringAdmins().isEmpty());
    }

    @Test
    void testQueueForWebSocket() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R004 No meaningful assertions found.
        //   Diffblue Cover was unable to create an assertion.
        //   Make sure that fields modified by queueForWebSocket()
        //   have package-private, protected, or public getters.
        //   See https://diff.blue/R004 to resolve this issue.

        AnonymousQueue actualQueueForWebSocketResult = (new MQConfig()).queueForWebSocket();
        assertTrue(actualQueueForWebSocketResult.shouldDeclare());
        assertTrue(actualQueueForWebSocketResult.isExclusive());
        assertFalse(actualQueueForWebSocketResult.isDurable());
        assertTrue(actualQueueForWebSocketResult.isAutoDelete());
        assertTrue(actualQueueForWebSocketResult.getDeclaringAdmins().isEmpty());
        assertEquals(1, actualQueueForWebSocketResult.getArguments().size());
    }

    @Test
    void testBindingSingle() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R004 No meaningful assertions found.
        //   Diffblue Cover was unable to create an assertion.
        //   Make sure that fields modified by bindingSingle(FanoutExchange, AnonymousQueue)
        //   have package-private, protected, or public getters.
        //   See https://diff.blue/R004 to resolve this issue.

        MQConfig mqConfig = new MQConfig();
        FanoutExchange fanoutExchange = new FanoutExchange("Name");
        Binding actualBindingSingleResult = mqConfig.bindingSingle(fanoutExchange, new AnonymousQueue());
        assertTrue(actualBindingSingleResult.getArguments().isEmpty());
        assertTrue(actualBindingSingleResult.shouldDeclare());
        assertEquals("", actualBindingSingleResult.getRoutingKey());
        assertEquals("Name", actualBindingSingleResult.getExchange());
        assertEquals(Binding.DestinationType.QUEUE, actualBindingSingleResult.getDestinationType());
        assertTrue(actualBindingSingleResult.getDeclaringAdmins().isEmpty());
    }
}

