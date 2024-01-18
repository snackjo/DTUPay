package dtupay.service.adapter.rest;

import dtupay.service.merchant.Merchant;
import dtupay.service.merchant.MerchantFacade;
import dtupay.service.report.MerchantReport;
import dtupay.service.report.ReportFacade;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/merchants")
public class MerchantResource {

    private final MerchantFacade merchantFacade = new MerchantFacade(new MessageQueueFactory().getQueue());
    private final ReportFacade reportFacade = new ReportFacade(new MessageQueueFactory().getQueue());

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Merchant registerMerchant(Merchant merchant) {
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
        return reportFacade.requestMerchantReport(dtuPayId);
    }

    @DELETE
    @Path("{dtuPayId}")
    public Response deregisterMerchant(@PathParam("dtuPayId") String dtuPayId) {
        merchantFacade.requestMerchantDeregistration(dtuPayId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}