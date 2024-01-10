package MerchantApp;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

public class MerchantDtuPay {
    private final WebTarget serverTarget;

    public MerchantDtuPay() {
        Client serverClient = ClientBuilder.newBuilder().build();
        this.serverTarget = serverClient.target("http://localhost:8080/");
    }

    public Merchant registerMerchant(Merchant merchant) {
        try {
            return serverTarget.path("merchants").request().post(Entity.entity(merchant, MediaType.APPLICATION_JSON), Merchant.class);
        } catch (NotFoundException e) {
            return e.getResponse().readEntity(Merchant.class);
        }
    }
}
