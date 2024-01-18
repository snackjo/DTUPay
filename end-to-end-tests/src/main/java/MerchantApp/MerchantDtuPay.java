package MerchantApp;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

// @author Oliver
public class MerchantDtuPay implements AutoCloseable {

    private final Client serverClient;
    private final WebTarget serverTarget;

    public MerchantDtuPay() {
        serverClient = ClientBuilder.newBuilder().build();
        this.serverTarget = serverClient.target("http://localhost:8080/merchants/");
    }

    public Merchant registerMerchant(Merchant merchant) {
        try {
            return serverTarget.request().post(Entity.entity(merchant, MediaType.APPLICATION_JSON), Merchant.class);
        } catch (NotFoundException e) {
            return e.getResponse().readEntity(Merchant.class);
        }
    }

    public String requestPayment(int paymentAmount, Token tokenReceivedFromCustomer, String merchantDtuPayId) throws Exception {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setToken(tokenReceivedFromCustomer);
        paymentRequest.setAmount(paymentAmount);
        try {
            return serverTarget
                    .path(merchantDtuPayId + "/payments")
                    .request()
                    .post(Entity.entity(paymentRequest, MediaType.APPLICATION_JSON), String.class);
        } catch (NotFoundException e) {
            throw new Exception(e);
        }
    }

    public MerchantApp.Report requestReport(String dtuPayId) {
        return serverTarget.path(dtuPayId + "/reports")
                .request()
                .get(MerchantApp.Report.class);
    }

    public Response deregisterMerchant(String dtuPayId) {
        return serverTarget.path(dtuPayId)
                .request()
                .delete();
    }

    @Override
    public void close() throws Exception {
        serverClient.close();
    }
}
