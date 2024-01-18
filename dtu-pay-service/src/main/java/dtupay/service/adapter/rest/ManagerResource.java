package dtupay.service.adapter.rest;

import dtupay.service.report.ManagerReport;
import dtupay.service.report.ReportFacade;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/reports")
public class ManagerResource {

    private final ReportFacade reportFacade = new ReportFacade(new MessageQueueFactory().getQueue());

    @GET
    @Produces("application/json")
    public ManagerReport requestReport(){
        return reportFacade.requestManagerReport();
    }
}
