package account.service;

import java.util.UUID;

import messaging.Event;
import messaging.MessageQueue;

public class AccountService {

	public static final String CUSTOMER_REGISTRATION_REQUESTED = "CustomerRegistrationRequested";
	public static final String CUSTOMER_REGISTERED = "CustomerRegistered";
	MessageQueue queue;

	public AccountService(MessageQueue q) {
		this.queue = q;
		this.queue.addHandler(CUSTOMER_REGISTRATION_REQUESTED, this::handleCustomerRegistrationRequested);
	}

	public void handleCustomerRegistrationRequested(Event ev) {
		Customer customer = ev.getArgument(0, Customer.class);
		CorrelationId correlationId = ev.getArgument(1, CorrelationId.class);

		customer.setDtuPayId(UUID.randomUUID().toString());
		AccountRepository accountRepository = AccountRepositoryFactory.getRepository();
		accountRepository.addCustomer(customer);

		Event event = new Event(CUSTOMER_REGISTERED, new Object[] { customer, correlationId });
		queue.publish(event);
	}
}
