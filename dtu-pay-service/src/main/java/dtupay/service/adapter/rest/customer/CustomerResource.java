package dtupay.service.adapter.rest.customer;

import dtupay.service.DtuPayException;
import dtupay.service.Token;
import dtupay.service.adapter.rest.MessageQueueFactory;
import dtupay.service.customer.Customer;
import dtupay.service.customer.CustomerFacade;
import dtupay.service.customer.CustomerReport;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

// @author Bastian
@Path("/customers")
@Tag(name = "Customer")
public class CustomerResource {

    private final CustomerFacade customerFacade = new CustomerFacade(new MessageQueueFactory().getQueue());

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    @APIResponse(
            responseCode = "201",
            content = @Content(mediaType = APPLICATION_JSON,
                    schema = @Schema(implementation = Customer.class)),
            description = "Customer was registered in DTU Pay"
    )
    public Response registerCustomer(Customer registrationRequest) {
        Customer customer = new Customer();
        customer.setCprNumber(registrationRequest.getCprNumber());
        customer.setFirstName(registrationRequest.getFirstName());
        customer.setLastName(registrationRequest.getLastName());
        customer.setAccountId(registrationRequest.getAccountId());
        return Response.status(201).entity(customerFacade.registerCustomer(customer)).build();
    }

    @POST
    @Path("{dtuPayId}" + "/tokens")
    @Consumes("application/json")
    @Produces("application/json")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    content = @Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(type = SchemaType.ARRAY, implementation = Token.class)),
                    description = "New tokens were generated for the customer"
            ),
            @APIResponse(
                    responseCode = "400",
                    content = @Content(mediaType = APPLICATION_JSON,
                            schema = @Schema(implementation = ErrorResponse.class)),
                    description = "No tokens were generated"
            )
    })
    public Response requestTokens(@PathParam("dtuPayId") String dtuPayId, TokenRequest tokenRequest) {
        try {
            return Response.ok(customerFacade.requestTokens(dtuPayId, tokenRequest.getTokenAmount())).build();
        } catch (DtuPayException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResponse(e.getMessage())).build();
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
    @APIResponse(responseCode = "204", description = "Customer was deregistered from DTU Pay")
    public Response deregisterCustomer(@PathParam("dtuPayId") String dtuPayId) {
        customerFacade.requestCustomerDeregistration(dtuPayId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
