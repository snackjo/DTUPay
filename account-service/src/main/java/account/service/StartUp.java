package account.service;

import messaging.implementations.RabbitMqQueue;

public class StartUp {
	public static void main(String[] args) {
		new StartUp().startUp();
	}

	private void startUp() {
		var mq = new RabbitMqQueue("rabbitMq");
		new AccountService(mq);
	}
}
