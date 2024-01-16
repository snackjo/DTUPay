package report.service;

import messaging.Event;
import messaging.MessageQueue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ReportService {
    public static final String PAYMENT_COMPLETED = "PaymentCompleted";
    private final List<Payment> payments = Collections.synchronizedList(new ArrayList<>());
    private final MessageQueue queue;

    public ReportService(MessageQueue q) {
        this.queue = q;

        this.queue.addHandler(PAYMENT_COMPLETED, this::handlePaymentCompletedEvent);
    }

    public List<Payment> getReports() {
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
}
