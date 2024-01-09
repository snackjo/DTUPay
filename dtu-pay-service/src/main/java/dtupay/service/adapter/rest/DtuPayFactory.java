package dtupay.service.adapter.rest;

import dtupay.service.DtuPayService;
import dtupay.service.StudentRegistrationService;
import messaging.implementations.RabbitMqQueue;

public class DtuPayFactory {
    static DtuPayService service = null;

    public DtuPayService getService() {
        if (service != null) {
            return service;
        }

        /*var mq = new RabbitMqQueue("rabbitMq");*/
        service = new DtuPayService(null);
        return service;
    }
}
