package dtupay.service.adapter.rest;

import dtupay.service.DTUPayService;
import dtupay.service.Report;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/reports")
public class ManagerResource {

    private final DTUPayService service = new DtuPayFactory().getService();

    @GET
    @Produces("application/json")
    public Report requestReport(){
        return service.requestManagerReport();
    }
}
