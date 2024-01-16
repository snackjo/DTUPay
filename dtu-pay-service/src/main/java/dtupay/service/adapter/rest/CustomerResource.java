package dtupay.service.adapter.rest;

import dtupay.service.Customer;
import dtupay.service.DTUPayException;
import dtupay.service.DTUPayService;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/customers")
public class CustomerResource {

	private final DTUPayService service = new DtuPayFactory().getService();

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Customer registerCustomer(Customer customer) {
		return service.registerCustomer(customer);
	}

	@POST
	@Path("{dtuPayId}" + "/tokens")
	@Consumes("application/json")
	@Produces("application/json")
	public Response requestTokens(@PathParam("dtuPayId") String dtuPayId, int tokenAmount) {
        try {
            return Response.ok(service.requestTokens(dtuPayId, tokenAmount)).build();
        } catch (DTUPayException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
