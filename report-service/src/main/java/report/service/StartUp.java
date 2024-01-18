package report.service;

import messaging.implementations.RabbitMqQueue;

public class StartUp {
    public static void main(String[] args) {
        new StartUp().startUp();
    }

    private void startUp() {
        RabbitMqQueue mq = new RabbitMqQueue("rabbitMq");
        ReportRepository reportRepository = new ReportRepository();
        new ReportService(mq, reportRepository);
    }
}
