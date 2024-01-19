package dtupay.service.adapter.rest;

import dtupay.service.merchant.Merchant;
import dtupay.service.merchant.MerchantFacade;
import dtupay.service.merchant.MerchantReport;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

// @author Oliver
@Path("/merchants")
@Tag(name = "Merchant")
public class MerchantResource {

    private final MerchantFacade merchantFacade = new MerchantFacade(new MessageQueueFactory().getQueue());

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Merchant registerMerchant(MerchantRegistrationRequest registrationRequest) {
        Merchant merchant = new Merchant();
        merchant.setCprNumber(registrationRequest.getCprNumber());
        merchant.setFirstName(registrationRequest.getFirstName());
        merchant.setLastName(registrationRequest.getLastName());
        merchant.setAccountId(registrationRequest.getAccountId());
        return merchantFacade.registerMerchant(merchant);
    }

    @POST
    @Path("{dtuPayId}" + "/payments")
    @Consumes("application/json")
    @Produces("text/plain")
    public String requestPayment(@PathParam("dtuPayId") String dtuPayId, PaymentRequest paymentRequest) {
        return merchantFacade.requestPayment(dtuPayId, paymentRequest.getToken(), paymentRequest.getAmount());
    }

    @GET
    @Path("{dtuPayId}" + "/reports")
    @Produces("application/json")
    public MerchantReport requestReport(@PathParam("dtuPayId") String dtuPayId) {
        return merchantFacade.requestMerchantReport(dtuPayId);
    }

    @DELETE
    @Path("{dtuPayId}")
    public Response deregisterMerchant(@PathParam("dtuPayId") String dtuPayId) {
        merchantFacade.requestMerchantDeregistration(dtuPayId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}