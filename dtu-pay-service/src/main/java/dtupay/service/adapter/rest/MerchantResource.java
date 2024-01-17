package dtupay.service.adapter.rest;

import dtupay.service.merchant.Merchant;
import dtupay.service.merchant.MerchantService;
import dtupay.service.report.Report;
import dtupay.service.report.ReportService;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/merchants")
public class MerchantResource {

    private final MerchantService service = new MerchantService(new MessageQueueFactory().getQueue());
    private final ReportService reportService = new ReportService(new MessageQueueFactory().getQueue());

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
    public Report requestReport(@PathParam("dtuPayId") String dtuPayId) {
        return reportService.requestMerchantReport(dtuPayId);
    }

    @DELETE
    @Path("{dtuPayId}")
    public Response deregisterMerchant(@PathParam("dtuPayId") String dtuPayId) {
        service.requestMerchantDeregistration(dtuPayId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}