package CustomerApp;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

public class CustomerDTUPay {
    private final WebTarget serverTarget;

    public CustomerDTUPay() {
        Client serverClient = ClientBuilder.newBuilder().build();
        this.serverTarget = serverClient.target("http://localhost:8080/");
    }

    public Customer registerCustomer(Customer customer) {
        try {
            return serverTarget.path("customers").request().post(Entity.entity(customer, MediaType.APPLICATION_JSON), Customer.class);
        } catch (NotFoundException e) {
            return e.getResponse().readEntity(Customer.class);
        }
    }
}
