package dtupay.service.manager;


import dtupay.service.CorrelationId;
import dtupay.service.EventNames;
import messaging.Event;
import messaging.MessageQueue;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

// @author Peter
public class ManagerFacade {
    private final Map<CorrelationId, CompletableFuture<ManagerReport>> managerReportCorrelations = new ConcurrentHashMap<>();
    private final MessageQueue queue;

    public ManagerFacade(MessageQueue q) {
        queue = q;
        queue.addHandler(EventNames.MANAGER_REPORT_GENERATED, this::handleManagerReportGenerated);
    }

    public ManagerReport requestManagerReport() {
        CorrelationId correlationId = CorrelationId.randomId();
        managerReportCorrelations.put(correlationId, new CompletableFuture<>());

        Event event = new Event(EventNames.MANAGER_REPORT_REQUESTED, new Object[]{correlationId});
        queue.publish(event);

        ManagerReport response = managerReportCorrelations.get(correlationId).join();
        managerReportCorrelations.remove(correlationId);
        return response;
    }

    public void handleManagerReportGenerated(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        ManagerReport report = event.getArgument(1, ManagerReport.class);
        managerReportCorrelations.get(correlationId).complete(report);
    }

}
