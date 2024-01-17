package report.service;

import messaging.Event;
import messaging.MessageQueue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ReportService {
    private final List<Payment> payments = Collections.synchronizedList(new ArrayList<>());
    private final MessageQueue queue;

    public ReportService(MessageQueue q) {
        this.queue = q;

        this.queue.addHandler(EventNames.PAYMENT_COMPLETED, this::handlePaymentCompletedEvent);
        this.queue.addHandler(EventNames.MANAGER_REPORT_REQUESTED, this::handleManagerReportRequested);
        this.queue.addHandler(EventNames.CUSTOMER_REPORT_REQUESTED, this::handleCustomerReportRequested);
        this.queue.addHandler(EventNames.MERCHANT_REPORT_REQUESTED, this::handleMerchantReportRequested);
    }

    public List<Payment> getManagerReport() {
        return payments;
    }

    private List<Payment> getCustomerReport(String customerDtuPayId) {
        return payments.stream()
                .filter(payment -> payment.getCustomerDtuPayId().equals(customerDtuPayId))
                .collect(Collectors.toList());
    }

    private List<Payment> getMerchantReport(String merchantDtuPayId) {
        return payments.stream()
                .filter(payment -> payment.getMerchantDtuPayId().equals(merchantDtuPayId))
                .map(payment -> new Payment(payment.getMerchantDtuPayId(), payment.getCustomerToken(), payment.getAmount()))
                .collect(Collectors.toList());
    }

    public void handlePaymentCompletedEvent(Event event) {
        Payment payment = new Payment();
        payment.setMerchantDtuPayId(event.getArgument(1, String.class));
        payment.setCustomerToken(event.getArgument(2, Token.class));
        payment.setAmount(event.getArgument(3, Integer.class));
        payment.setCustomerDtuPayId(event.getArgument(4, String.class));
        payments.add(payment);
    }

    public void handleManagerReportRequested(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        Report report = new Report();
        report.setPayments(getManagerReport());
        Event publishEvent = new Event(EventNames.MANAGER_REPORT_GENERATED, new Object[]{ correlationId, report});
        queue.publish(publishEvent);
    }
    public void handleCustomerReportRequested(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        String customerDtuPayId = event.getArgument(1, String.class);
        Report report = new Report();
        report.setPayments(getCustomerReport(customerDtuPayId));
        Event publishEvent = new Event(EventNames.CUSTOMER_REPORT_GENERATED, new Object[]{correlationId, report});
        queue.publish(publishEvent);
    }

    public void handleMerchantReportRequested(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        String merchantDtuPayId = event.getArgument(1, String.class);
        Report report = new Report();
        report.setPayments(getMerchantReport(merchantDtuPayId));
        Event publishEvent = new Event(EventNames.MERCHANT_REPORT_GENERATED, new Object[]{correlationId, report});
        queue.publish(publishEvent);
    }
}
