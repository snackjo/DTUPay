package dtupay.service.adapter.rest;

import dtupay.service.manager.ManagerFacade;
import dtupay.service.manager.ManagerReport;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/reports")
public class ManagerResource {

    private final ManagerFacade managerFacade = new ManagerFacade(new MessageQueueFactory().getQueue());

    @GET
    @Produces("application/json")
    public ManagerReport requestReport() {
        return managerFacade.requestManagerReport();
    }
}
