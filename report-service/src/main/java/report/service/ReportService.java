package report.service;

import messaging.Event;
import messaging.MessageQueue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ReportService {
    public static final String PAYMENT_COMPLETED = "PaymentCompleted";
    public static final String MANAGER_REPORT_REQUESTED = "ManagerReportRequested";
    public static final String MANAGER_REPORT_GENERATED = "ManagerReportGenerated";
    public static final String CUSTOMER_REPORT_REQUESTED = "CustomerReportRequested";
    public static final String CUSTOMER_REPORT_GENERATED = "CustomerReportGenerated";
    public static final String MERCHANT_REPORT_REQUESTED = "MerchantReportRequested";
    public static final String MERCHANT_REPORT_GENERATED = "MerchantReportGenerated";
    private final List<Payment> payments = Collections.synchronizedList(new ArrayList<>());
    private final MessageQueue queue;

    public ReportService(MessageQueue q) {
        this.queue = q;

        this.queue.addHandler(PAYMENT_COMPLETED, this::handlePaymentCompletedEvent);
        this.queue.addHandler(MANAGER_REPORT_REQUESTED, this::handleManagerReportRequested);
        this.queue.addHandler(CUSTOMER_REPORT_REQUESTED, this::handleCustomerReportRequested);
        this.queue.addHandler(MERCHANT_REPORT_REQUESTED, this::handleMerchantReportRequested);
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
                .collect(Collectors.toList());
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
    public void handleCustomerReportRequested(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        String customerDtuPayId = event.getArgument(1, String.class);
        Event publishEvent = new Event(CUSTOMER_REPORT_GENERATED, new Object[]{correlationId, getCustomerReport(customerDtuPayId)});
        queue.publish(publishEvent);
    }

    public void handleMerchantReportRequested(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        String merchantDtuPayId = event.getArgument(1, String.class);
        Event publishEvent = new Event(MERCHANT_REPORT_GENERATED, new Object[]{correlationId, getMerchantReport(merchantDtuPayId)});
        queue.publish(publishEvent);
    }
}
