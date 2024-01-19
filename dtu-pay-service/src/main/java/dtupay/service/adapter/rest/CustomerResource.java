package dtupay.service.adapter.rest;

import dtupay.service.DtuPayException;
import dtupay.service.customer.Customer;
import dtupay.service.customer.CustomerFacade;
import dtupay.service.customer.CustomerReport;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

// @author Bastian
@Path("/customers")
@Tag(name = "Customer")
public class CustomerResource {

    private final CustomerFacade customerFacade = new CustomerFacade(new MessageQueueFactory().getQueue());


    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Customer registerCustomer(Customer registrationRequest) {
        Customer customer = new Customer();
        customer.setCprNumber(registrationRequest.getCprNumber());
        customer.setFirstName(registrationRequest.getFirstName());
        customer.setLastName(registrationRequest.getLastName());
        customer.setAccountId(registrationRequest.getAccountId());
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
    public CustomerReport requestReport(@PathParam("dtuPayId") String dtuPayId) {
        return customerFacade.requestCustomerReport(dtuPayId);
    }

    @DELETE
    @Path("{dtuPayId}")
    public Response deregisterCustomer(@PathParam("dtuPayId") String dtuPayId) {
        customerFacade.requestCustomerDeregistration(dtuPayId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
