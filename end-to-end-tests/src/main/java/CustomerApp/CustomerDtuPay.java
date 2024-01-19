package CustomerApp;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

// @author Carl
public class CustomerDtuPay implements AutoCloseable {

    private final Client serverClient;
    private final WebTarget serverTarget;

    public CustomerDtuPay() {
        serverClient = ClientBuilder.newBuilder().build();
        this.serverTarget = serverClient.target("http://localhost:8080/customers/");
    }

    public Customer registerCustomer(Customer customer)  {
        return serverTarget.request().post(Entity.entity(customer, MediaType.APPLICATION_JSON), Customer.class);
    }

    public List<Token> requestTokens(Customer customer, int tokenAmount) throws DtuPayException {
        try {
            return serverTarget
                    .path(customer.getDtuPayId() + "/tokens")
                    .request()
                    .post(Entity.entity(new TokenRequest(tokenAmount), MediaType.APPLICATION_JSON),new GenericType<List<Token>>(){});
        } catch (BadRequestException e) {
            throw new DtuPayException(e.getResponse().readEntity(ErrorResponse.class).getMessage());
        }
    }

    public Report requestReport(String dtuPayId) {
        return serverTarget.path(dtuPayId + "/reports")
                .request()
                .get(Report.class);
    }

    public Response deregisterCustomer(String dtuPayId) {
        return serverTarget.path(dtuPayId)
                .request()
                .delete();
    }

    @Override
    public void close() throws Exception {
        serverClient.close();
    }
}
