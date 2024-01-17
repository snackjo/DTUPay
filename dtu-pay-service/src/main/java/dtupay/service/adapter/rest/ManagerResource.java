package dtupay.service.adapter.rest;

import dtupay.service.report.ReportService;
import dtupay.service.Report;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/reports")
public class ManagerResource {

    private final ReportService reportService = new ReportService(new MessageQueueFactory().getQueue());

    @GET
    @Produces("application/json")
    public Report requestReport(){
        return reportService.requestManagerReport();
    }
}
