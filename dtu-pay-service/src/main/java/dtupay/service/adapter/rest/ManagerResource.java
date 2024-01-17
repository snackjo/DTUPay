package dtupay.service.adapter.rest;

import dtupay.service.ManagerService;
import dtupay.service.Report;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/reports")
public class ManagerResource {

    private final ManagerService service = new ManagerService(new MessageQueueFactory().getQueue());

    @GET
    @Produces("application/json")
    public Report requestReport(){
        return service.requestManagerReport();
    }
}
