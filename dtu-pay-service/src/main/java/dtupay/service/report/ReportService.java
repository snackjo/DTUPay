package dtupay.service.report;


import dtupay.service.CorrelationId;
import dtupay.service.EventNames;
import messaging.Event;
import messaging.MessageQueue;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ReportService {
    private final Map<CorrelationId, CompletableFuture<Report>> managerReportCorrelations = new ConcurrentHashMap<>();
    private final Map<CorrelationId, CompletableFuture<Report>> merchantReportCorrelations = new ConcurrentHashMap<>();
    private final Map<CorrelationId, CompletableFuture<Report>> customerReportCorrelations = new ConcurrentHashMap<>();
    private final MessageQueue queue;

    public ReportService(MessageQueue q) {
        queue = q;
        queue.addHandler(EventNames.MANAGER_REPORT_GENERATED, this::handleManagerReportGenerated);
        queue.addHandler(EventNames.MERCHANT_REPORT_GENERATED, this::handleMerchantReportGenerated);
        queue.addHandler(EventNames.CUSTOMER_REPORT_GENERATED, this::handleCustomerReportGenerated);
    }

    public Report requestManagerReport() {
        CorrelationId correlationId = CorrelationId.randomId();
        managerReportCorrelations.put(correlationId, new CompletableFuture<>());

        Event event = new Event(EventNames.MANAGER_REPORT_REQUESTED, new Object[]{correlationId});
        queue.publish(event);

        Report response = managerReportCorrelations.get(correlationId).join();
        managerReportCorrelations.remove(correlationId);
        return response;
    }

    public void handleManagerReportGenerated(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        Report report = event.getArgument(1, Report.class);
        managerReportCorrelations.get(correlationId).complete(report);
    }

    public Report requestMerchantReport(String merchantDtuPayId) {
        CorrelationId correlationId = CorrelationId.randomId();
        merchantReportCorrelations.put(correlationId, new CompletableFuture<>());

        Event event = new Event(EventNames.MERCHANT_REPORT_REQUESTED, new Object[]{correlationId, merchantDtuPayId});
        queue.publish(event);

        Report response = merchantReportCorrelations.get(correlationId).join();
        merchantReportCorrelations.remove(correlationId);
        return response;
    }

    public void handleMerchantReportGenerated(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        Report report = event.getArgument(1, Report.class);
        merchantReportCorrelations.get(correlationId).complete(report);
    }

    public Report requestCustomerReport(String customerDtuPayId) {
        CorrelationId correlationId = CorrelationId.randomId();
        customerReportCorrelations.put(correlationId, new CompletableFuture<>());

        Event event = new Event(EventNames.CUSTOMER_REPORT_REQUESTED, new Object[]{correlationId, customerDtuPayId});
        queue.publish(event);

        Report response = customerReportCorrelations.get(correlationId).join();
        customerReportCorrelations.remove(correlationId);
        return response;
    }

    public void handleCustomerReportGenerated(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        Report report = event.getArgument(1, Report.class);
        customerReportCorrelations.get(correlationId).complete(report);
    }
}
