package dtupay.service.adapter.rest;

import dtupay.service.DTUPayService;
import dtupay.service.Payment;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

@Path("/reports")
public class ManagerResource {

    private final DTUPayService service = new DtuPayFactory().getService();

    @GET
    @Produces("application/json")
    public List<Payment> requestReport(){
        return service.requestManagerReport();
    }
}
