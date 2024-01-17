package ManagerApp;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

public class ManagerDtuPay {

    private final WebTarget serverTarget;

    public ManagerDtuPay() {
        Client serverClient = ClientBuilder.newBuilder().build();
        this.serverTarget = serverClient.target("http://localhost:8080/reports");
    }

    public Report requestReport()  {
        return serverTarget.request().get(Report.class);
    }
}
