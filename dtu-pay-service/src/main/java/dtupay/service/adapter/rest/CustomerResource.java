package dtupay.service.adapter.rest;

import dtupay.service.Customer;
import dtupay.service.DtuPayService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/customers")
public class CustomerResource {

	private final DtuPayService service = new DtuPayFactory().getService();

	@POST
	@Consumes("application/json")
	@Produces("text/plain")
	public String registerStudent(Customer customer) {
		return service.register(customer);
	}
}
