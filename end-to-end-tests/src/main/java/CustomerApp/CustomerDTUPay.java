package CustomerApp;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

public class CustomerDTUPay {
    private final WebTarget serverTarget;

    public CustomerDTUPay() {
        Client serverClient = ClientBuilder.newBuilder().build();
        this.serverTarget = serverClient.target("http://localhost:8080/");
    }

    public Customer registerCustomer(Customer customer) throws Exception {
        try {
            return serverTarget.path("customers").request().post(Entity.entity(customer, MediaType.APPLICATION_JSON), Customer.class);
        } catch (NotFoundException e) {
            throw new Exception();
        }
    }

    public List<Token> requestTokens(Customer customer, int tokenAmount) throws Exception {
        try {
            return serverTarget
                    .path("customers/" + customer.getDtuPayId() + "/tokens")
                    .request()
                    .post(Entity.entity(tokenAmount, MediaType.APPLICATION_JSON),new GenericType<List<Token>>(){});
        } catch (NotFoundException e) {
            throw new Exception(e);
        }
    }
}
