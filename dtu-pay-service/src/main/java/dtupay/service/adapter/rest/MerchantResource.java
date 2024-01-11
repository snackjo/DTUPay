package dtupay.service.adapter.rest;

import dtupay.service.DTUPayService;
import dtupay.service.Merchant;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/merchants")
public class MerchantResource {

    private final DTUPayService service = new DtuPayFactory().getService();

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Merchant registerMerchant(Merchant merchant) {
        return service.registerMerchant(merchant);
    }
}