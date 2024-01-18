package report.service;

import messaging.Event;
import messaging.MessageQueue;

// @author Emil
public class ReportService {
    private final MessageQueue queue;
    private final ReportRepository reportRepository;

    public ReportService(MessageQueue q, ReportRepository reportRepository) {
        this.queue = q;
        this.reportRepository = reportRepository;

        this.queue.addHandler(EventNames.PAYMENT_COMPLETED, this::handlePaymentCompletedEvent);
        this.queue.addHandler(EventNames.MANAGER_REPORT_REQUESTED, this::handleManagerReportRequested);
        this.queue.addHandler(EventNames.CUSTOMER_REPORT_REQUESTED, this::handleCustomerReportRequested);
        this.queue.addHandler(EventNames.MERCHANT_REPORT_REQUESTED, this::handleMerchantReportRequested);
    }

    public void handlePaymentCompletedEvent(Event event) {
        Payment payment = new Payment();
        payment.setMerchantDtuPayId(event.getArgument(1, String.class));
        payment.setCustomerToken(event.getArgument(2, Token.class));
        payment.setAmount(event.getArgument(3, Integer.class));
        payment.setCustomerDtuPayId(event.getArgument(4, String.class));

        reportRepository.storePayment(payment);
    }

    public void handleManagerReportRequested(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        generateManagerReport(correlationId);
    }

    private void generateManagerReport(CorrelationId correlationId) {
        ManagerReport report = reportRepository.getManagerReport();
        Event publishEvent = new Event(EventNames.MANAGER_REPORT_GENERATED, new Object[]{correlationId, report});
        queue.publish(publishEvent);
    }

    public void handleCustomerReportRequested(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        String customerDtuPayId = event.getArgument(1, String.class);
        generateCustomerReport(customerDtuPayId, correlationId);
    }

    private void generateCustomerReport(String customerDtuPayId, CorrelationId correlationId) {
        CustomerReport report = reportRepository.getCustomerReport(customerDtuPayId);
        Event publishEvent = new Event(EventNames.CUSTOMER_REPORT_GENERATED, new Object[]{correlationId, report});
        queue.publish(publishEvent);
    }

    public void handleMerchantReportRequested(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        String merchantDtuPayId = event.getArgument(1, String.class);
        generateMerchantReport(merchantDtuPayId, correlationId);
    }

    private void generateMerchantReport(String merchantDtuPayId, CorrelationId correlationId) {
        MerchantReport report = reportRepository.getMerchantReport(merchantDtuPayId);
        Event publishEvent = new Event(EventNames.MERCHANT_REPORT_GENERATED, new Object[]{correlationId, report});
        queue.publish(publishEvent);
    }
}
