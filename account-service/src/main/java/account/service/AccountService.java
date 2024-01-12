package account.service;

import java.util.UUID;

import messaging.Event;
import messaging.MessageQueue;

public class AccountService {

	public static final String CUSTOMER_REGISTRATION_REQUESTED = "CustomerRegistrationRequested";
	public static final String CUSTOMER_REGISTERED = "CustomerRegistered";
	private static final String MERCHANT_REGISTERED = "MerchantRegistered";
	private static final String MERCHANT_REGISTRATION_REQUESTED = "MerchantRegistrationRequested";
	private static final String TOKEN_MATCH_FOUND = "TokenMatchFound";
	private static final String CUSTOMER_BANK_ACCOUNT_FOUND = "CustomerBankAccountFound";
	private static final String PAYMENT_REQUESTED = "PaymentRequested";
	private static final String MERCHANT_BANK_ACCOUNT_FOUND = "MerchantBankAccountFound";


	MessageQueue queue;
	private AccountRepository accountRepository;

	public AccountService(MessageQueue q) {
		this.queue = q;
		accountRepository = AccountRepositoryFactory.getRepository();

		this.queue.addHandler(CUSTOMER_REGISTRATION_REQUESTED, this::handleCustomerRegistrationRequested);
		this.queue.addHandler(MERCHANT_REGISTRATION_REQUESTED, this::handleMerchantRegistrationRequested);
		this.queue.addHandler(TOKEN_MATCH_FOUND, this::handleTokenMatchFound);
		this.queue.addHandler(PAYMENT_REQUESTED, this::handlePaymentRequested);
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

    public void handleMerchantRegistrationRequested(Event event) {
		Merchant merchant = event.getArgument(0, Merchant.class);
		CorrelationId correlationId = event.getArgument(1, CorrelationId.class);

		merchant.setDtuPayId(UUID.randomUUID().toString());
		accountRepository.addMerchant(merchant);

		Event publishEvent = new Event(MERCHANT_REGISTERED, new Object[] { merchant, correlationId });
		queue.publish(publishEvent);
    }

	public void handleTokenMatchFound(Event event) {
		String customerDtuPayId = event.getArgument(0, String.class);
		CorrelationId correlationId = event.getArgument(1, CorrelationId.class);

		String customerAccount = accountRepository.getCustomerAccount(customerDtuPayId);
		Event publishEvent = new Event(CUSTOMER_BANK_ACCOUNT_FOUND, new Object[] {customerAccount, correlationId});
		queue.publish(publishEvent);
	}

	public void handlePaymentRequested(Event event) {
		String merchantDtuPayId = event.getArgument(0, String.class);
		CorrelationId correlationId = event.getArgument(3, CorrelationId.class);

		String merchantAccount = accountRepository.getMerchantAccount(merchantDtuPayId);
		Event publishEvent = new Event(MERCHANT_BANK_ACCOUNT_FOUND, new Object[] {merchantAccount, correlationId});
		queue.publish(publishEvent);
	}
}
