package ManagerApp;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

public class ManagerClient {

    private final WebTarget serverTarget;

    public ManagerClient() {
        Client serverClient = ClientBuilder.newBuilder().build();
        this.serverTarget = serverClient.target("http://localhost:8080/");
    }

    public Report requestReport()  {
        return serverTarget.path("reports").request().get(Report.class);
    }
}
