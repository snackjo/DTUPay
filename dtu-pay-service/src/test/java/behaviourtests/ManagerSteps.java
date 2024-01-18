package behaviourtests;

import dtupay.service.EventNames;
import dtupay.service.manager.ManagerFacade;
import dtupay.service.manager.ManagerReport;
import dtupay.service.manager.ManagerReportEntry;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class ManagerSteps {
    private final PublishedEventHolder publishedEventHolder;
    private final MessageQueue queueMock = mock(MessageQueue.class);
    private final ManagerFacade managerFacade = new ManagerFacade(queueMock);
    private Thread requestThread;
    private ManagerReport managerReportGenerated;

    public ManagerSteps(PublishedEventHolder publishedEventHolder) {
        this.publishedEventHolder = publishedEventHolder;
        this.publishedEventHolder.setQueue(queueMock);
    }


    @When("a manager requests a report")
    public void aManagerRequestsAReport() {
        requestThread = new Thread(() -> {
            managerReportGenerated = managerFacade.requestManagerReport();
        });
        requestThread.start();
    }

    @When("a ManagerReportGenerated event is received")
    public void aManagerReportGeneratedEventIsReceived() {
        ManagerReport report = new ManagerReport();
        List<ManagerReportEntry> payments = new ArrayList<>();
        payments.add(new ManagerReportEntry());

        report.setPayments(payments);
        managerFacade.handleManagerReportGenerated(new Event(EventNames.MANAGER_REPORT_GENERATED,
                new Object[]{publishedEventHolder.getCorrelationId(), report}));
    }

    @Then("a manager report is returned")
    public void aManagerReportIsReturned() throws InterruptedException {
        requestThread.join();
        assertEquals(1, managerReportGenerated.getPayments().size());
    }
}
