package ManagerApp;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

import java.util.List;

public class ManagerClient {

    private final WebTarget serverTarget;

    public ManagerClient() {
        Client serverClient = ClientBuilder.newBuilder().build();
        this.serverTarget = serverClient.target("http://localhost:8080/");
    }

    public List<Payment> requestReport()  {
        return serverTarget.path("reports").request().get(List.class);
    }
}
