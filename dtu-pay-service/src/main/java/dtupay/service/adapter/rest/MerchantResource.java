package dtupay.service.adapter.rest;

import dtupay.service.DTUPayService;
import dtupay.service.Merchant;
import dtupay.service.PaymentRequest;
import dtupay.service.Report;

import javax.ws.rs.*;

@Path("/merchants")
public class MerchantResource {

    private final DTUPayService service = new DtuPayFactory().getService();

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Merchant registerMerchant(Merchant merchant) {
        return service.registerMerchant(merchant);
    }

    @POST
    @Path("{dtuPayId}" + "/payments")
    @Consumes("application/json")
    @Produces("text/plain")
    public String requestPayment(@PathParam("dtuPayId") String dtuPayId, PaymentRequest paymentRequest) {
        return service.requestPayment(dtuPayId, paymentRequest.getToken(), paymentRequest.getAmount());
    }

    @GET
    @Path("{dtuPayId}" + "/reports")
    @Produces("application/json")
    public Report requestReport(@PathParam("dtuPayId") String dtuPayId){
        return service.requestMerchantReport(dtuPayId);
    }
}