package dtupay.service.adapter.rest;

import dtupay.service.DtuPayException;
import dtupay.service.customer.Customer;
import dtupay.service.customer.CustomerFacade;
import dtupay.service.report.CustomerReport;
import dtupay.service.report.ReportFacade;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/customers")
public class CustomerResource {

	private final CustomerFacade customerFacade = new CustomerFacade(new MessageQueueFactory().getQueue());
	private final ReportFacade reportFacade = new ReportFacade(new MessageQueueFactory().getQueue());

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Customer registerCustomer(Customer customer) {
		return customerFacade.registerCustomer(customer);
	}

	@POST
	@Path("{dtuPayId}" + "/tokens")
	@Consumes("application/json")
	@Produces("application/json")
	public Response requestTokens(@PathParam("dtuPayId") String dtuPayId, int tokenAmount) {
        try {
            return Response.ok(customerFacade.requestTokens(dtuPayId, tokenAmount)).build();
        } catch (DtuPayException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

	@GET
	@Path("{dtuPayId}" + "/reports")
	@Produces("application/json")
	public CustomerReport requestReport(@PathParam("dtuPayId") String dtuPayId){
		return reportFacade.requestCustomerReport(dtuPayId);
	}

	@DELETE
	@Path("{dtuPayId}")
	public Response deregisterCustomer(@PathParam("dtuPayId") String dtuPayId) {
		customerFacade.requestCustomerDeregistration(dtuPayId);
		return Response.status(Response.Status.NO_CONTENT).build();
	}
}
