package token.service;

import messaging.implementations.RabbitMqQueue;

public class StartUp {
    public static void main(String[] args) {
        new StartUp().startUp();
    }

    private void startUp() {
        RabbitMqQueue mq = new RabbitMqQueue("rabbitMq");
        CustomerRepository customerRepository = new CustomerRepository();
        new TokenService(mq, customerRepository);
    }
}
