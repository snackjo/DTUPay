package dtupay.service.adapter.rest;

import dtupay.service.Customer;
import dtupay.service.DTUPayService;
import dtupay.service.Token;

import javax.ws.rs.*;
import java.util.List;

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
	public List<Token> requestTokens(@PathParam("dtuPayId") String dtuPayId, int tokenAmount) {
		return service.requestTokens(dtuPayId, tokenAmount);
	}
}
