package dtupay.service;


import messaging.Event;
import messaging.MessageQueue;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ManagerService {
    private final Map<CorrelationId, CompletableFuture<Report>> managerReportCorrelations = new ConcurrentHashMap<>();
    private final MessageQueue queue;

    public ManagerService(MessageQueue q) {
        queue = q;
        queue.addHandler(EventNames.MANAGER_REPORT_GENERATED, this::handleManagerReportGenerated);
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
}
