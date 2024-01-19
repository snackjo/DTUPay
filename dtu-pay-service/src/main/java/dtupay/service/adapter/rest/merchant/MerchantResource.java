package dtupay.service.adapter.rest.merchant;

import dtupay.service.adapter.rest.MessageQueueFactory;
import dtupay.service.merchant.Merchant;
import dtupay.service.merchant.MerchantFacade;
import dtupay.service.merchant.MerchantReport;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

// @author Oliver
@Path("/merchants")
@Tag(name = "Merchant")
public class MerchantResource {

    private final MerchantFacade merchantFacade = new MerchantFacade(new MessageQueueFactory().getQueue());

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    @APIResponse(
            responseCode = "201",
            content = @Content(mediaType = APPLICATION_JSON,
                    schema = @Schema(implementation = Merchant.class)),
            description = "Merchant was registered in DTU Pay"
    )
    public Response registerMerchant(MerchantRegistrationRequest registrationRequest) {
        Merchant merchant = new Merchant();
        merchant.setCprNumber(registrationRequest.getCprNumber());
        merchant.setFirstName(registrationRequest.getFirstName());
        merchant.setLastName(registrationRequest.getLastName());
        merchant.setAccountId(registrationRequest.getAccountId());
        return Response.status(201).entity(merchantFacade.registerMerchant(merchant)).build();
    }

    @POST
    @Path("{dtuPayId}" + "/payments")
    @Consumes("application/json")
    @Produces("application/json")
    public PaymentResponse requestPayment(@PathParam("dtuPayId") String dtuPayId, PaymentRequest paymentRequest) {
        return new PaymentResponse(merchantFacade.requestPayment(dtuPayId, paymentRequest.getToken(), paymentRequest.getAmount()));
    }

    @GET
    @Path("{dtuPayId}" + "/reports")
    @Produces("application/json")
    public MerchantReport requestReport(@PathParam("dtuPayId") String dtuPayId) {
        return merchantFacade.requestMerchantReport(dtuPayId);
    }

    @DELETE
    @Path("{dtuPayId}")
    @APIResponse(responseCode = "204", description = "Merchant was deregistered from DTU Pay")
    public Response deregisterMerchant(@PathParam("dtuPayId") String dtuPayId) {
        merchantFacade.requestMerchantDeregistration(dtuPayId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}