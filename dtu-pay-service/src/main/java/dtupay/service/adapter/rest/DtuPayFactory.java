package dtupay.service.adapter.rest;

import dtupay.service.DTUPayService;
import messaging.implementations.RabbitMqQueue;

public class DtuPayFactory {
    static DTUPayService service = null;

    public DTUPayService getService() {
        if (service != null) {
            return service;
        }

        var mq = new RabbitMqQueue("rabbitMq");
        service = new DTUPayService(mq);
        return service;
    }
}
