package dtupay.service.adapter.rest;

import dtupay.service.customer.Customer;
import dtupay.service.customer.CustomerService;
import dtupay.service.DtuPayException;
import dtupay.service.report.Report;
import dtupay.service.report.ReportService;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/customers")
public class CustomerResource {

	private final CustomerService service = new CustomerService(new MessageQueueFactory().getQueue());
	private final ReportService reportService = new ReportService(new MessageQueueFactory().getQueue());

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
        } catch (DtuPayException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

	@GET
	@Path("{dtuPayId}" + "/reports")
	@Produces("application/json")
	public Report requestReport(@PathParam("dtuPayId") String dtuPayId){
		return reportService.requestCustomerReport(dtuPayId);
	}

	@DELETE
	@Path("{dtuPayId}")
	public Response deregisterCustomer(@PathParam("dtuPayId") String dtuPayId) {
		service.requestCustomerDeregistration(dtuPayId);
		return Response.status(Response.Status.NO_CONTENT).build();
	}
}
