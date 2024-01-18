package account.service;

import messaging.implementations.RabbitMqQueue;

// @author Peter
public class StartUp {
    public static void main(String[] args) {
        new StartUp().startUp();
    }

    private void startUp() {
        RabbitMqQueue mq = new RabbitMqQueue("rabbitMq");
        AccountRepository accountRepository = new AccountRepository();
        new AccountService(mq, accountRepository);
    }
}
