package report.service;

import messaging.Event;
import messaging.MessageQueue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReportService {
    public static final String PAYMENT_COMPLETED = "PaymentCompleted";
    public static final String MANAGER_REPORT_REQUESTED = "ManagerReportRequested";
    public static final String MANAGER_REPORT_GENERATED = "ManagerReportGenerated";
    private final List<Payment> payments = Collections.synchronizedList(new ArrayList<>());
    private final MessageQueue queue;

    public ReportService(MessageQueue q) {
        this.queue = q;

        this.queue.addHandler(PAYMENT_COMPLETED, this::handlePaymentCompletedEvent);
        this.queue.addHandler(MANAGER_REPORT_REQUESTED, this::handleManagerReportRequested);
    }

    public List<Payment> getManagerReport() {
        return payments;
    }

    public void handlePaymentCompletedEvent(Event event) {
        Payment payment = new Payment();
        payment.setMerchantDtuPayId(event.getArgument(1, String.class));
        payment.setCustomerToken(event.getArgument(2, String.class));
        payment.setAmount(event.getArgument(3, Integer.class));
        payment.setCustomerDtuPayId(event.getArgument(4, String.class));
        payments.add(payment);
    }

    public void handleManagerReportRequested(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        Event publishEvent = new Event(MANAGER_REPORT_GENERATED, new Object[]{ correlationId, getManagerReport() });
        queue.publish(publishEvent);
    }
}
