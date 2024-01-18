package ManagerApp;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

// @author Bastian
public class ManagerDtuPay implements AutoCloseable {

    private final Client serverClient;
    private final WebTarget serverTarget;

    public ManagerDtuPay() {
        serverClient = ClientBuilder.newBuilder().build();
        this.serverTarget = serverClient.target("http://localhost:8080/reports");
    }

    public Report requestReport()  {
        return serverTarget.request().get(Report.class);
    }

    @Override
    public void close() throws Exception {
        serverClient.close();
    }
}
