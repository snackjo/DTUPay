package dtupay.service.adapter.rest;

import dtupay.service.Customer;
import dtupay.service.DtuPayService;
import dtupay.service.Token;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

@Path("/customers")
public class CustomerResource {

	private final DtuPayService service = new DtuPayFactory().getService();

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Customer registerCustomer(Customer customer) {
		return service.registerCustomer(customer);
	}

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public List<Token> requestTokens(String dtuPayId, int tokenAmount) {
		return
	}
}
