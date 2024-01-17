package dtupay.service.adapter.rest;

import dtupay.service.DtuPayService;

public class DtuPayFactory {
    static DtuPayService service = null;

    public DtuPayService getService() {
        if (service != null) {
            return service;
        }

        var mq = new MessageQueueFactory().getQueue();
        service = new DtuPayService(mq);
        return service;
    }
}
