package CustomerApp;

import jakarta.ws.rs.BadRequestException;
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

    public Customer registerCustomer(Customer customer)  {
        return serverTarget.path("customers").request().post(Entity.entity(customer, MediaType.APPLICATION_JSON), Customer.class);
    }

    public List<Token> requestTokens(Customer customer, int tokenAmount) throws DTUPayException {
        try {
            return serverTarget
                    .path("customers/" + customer.getDtuPayId() + "/tokens")
                    .request()
                    .post(Entity.entity(tokenAmount, MediaType.APPLICATION_JSON),new GenericType<List<Token>>(){});
        } catch (BadRequestException e) {
            throw new DTUPayException(e.getMessage());
        }
    }
}
