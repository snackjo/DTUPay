package payment.service;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceService;
import messaging.implementations.RabbitMqQueue;

public class StartUp {
    public static void main(String[] args) {
        new StartUp().startUp();
    }

    private void startUp() {
        var mq = new RabbitMqQueue("rabbitMq");
        BankService bank = new BankServiceService().getBankServicePort();
        new PaymentService(mq, bank);
    }
}
