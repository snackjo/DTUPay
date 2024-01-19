package dtupay.service.adapter.rest.manager;

import dtupay.service.adapter.rest.MessageQueueFactory;
import dtupay.service.manager.ManagerFacade;
import dtupay.service.manager.ManagerReport;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

// @author Carl
@Path("/reports")
@Tag(name = "Manager")
public class ManagerResource {

    private final ManagerFacade managerFacade = new ManagerFacade(new MessageQueueFactory().getQueue());

    @GET
    @Produces("application/json")
    public ManagerReport requestReport() {
        return managerFacade.requestManagerReport();
    }
}
