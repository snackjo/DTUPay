package dtupay.service.adapter.rest;

import messaging.MessageQueue;
import messaging.implementations.RabbitMqQueue;

public class MessageQueueFactory {
    static MessageQueue queue = null;

    public MessageQueue getQueue() {
        if (queue != null) {
            return queue;
        }

        queue = new RabbitMqQueue("rabbitMq");
        return queue;
    }
}
