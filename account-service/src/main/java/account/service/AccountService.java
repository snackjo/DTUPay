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
	private final AccountRepository accountRepository;

	public AccountService(MessageQueue q, AccountRepository accountRepository) {
		this.queue = q;
		this.accountRepository = accountRepository;

		this.queue.addHandler(CUSTOMER_REGISTRATION_REQUESTED, this::handleCustomerRegistrationRequested);
		this.queue.addHandler(MERCHANT_REGISTRATION_REQUESTED, this::handleMerchantRegistrationRequested);
		this.queue.addHandler(TOKEN_MATCH_FOUND, this::handleTokenMatchFound);
		this.queue.addHandler(PAYMENT_REQUESTED, this::handlePaymentRequested);
	}

	public void handleCustomerRegistrationRequested(Event ev) {
		CorrelationId correlationId = ev.getArgument(0, CorrelationId.class);
		Customer customer = ev.getArgument(1, Customer.class);

		customer.setDtuPayId(UUID.randomUUID().toString());
		accountRepository.addCustomer(customer);

		Event event = new Event(CUSTOMER_REGISTERED, new Object[] { correlationId, customer });
		queue.publish(event);
	}

    public void handleMerchantRegistrationRequested(Event event) {
		CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
		Merchant merchant = event.getArgument(1, Merchant.class);

		merchant.setDtuPayId(UUID.randomUUID().toString());
		accountRepository.addMerchant(merchant);

		Event publishEvent = new Event(MERCHANT_REGISTERED, new Object[] { correlationId, merchant });
		queue.publish(publishEvent);
    }

	public void handleTokenMatchFound(Event event) {
		CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
		String customerDtuPayId = event.getArgument(1, String.class);

		String customerAccount = accountRepository.getCustomerAccount(customerDtuPayId);
		Event publishEvent = new Event(CUSTOMER_BANK_ACCOUNT_FOUND, new Object[] {correlationId, customerAccount, customerDtuPayId});
		queue.publish(publishEvent);
	}

	public void handlePaymentRequested(Event event) {
		CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
		String merchantDtuPayId = event.getArgument(1, String.class);

		String merchantAccount = accountRepository.getMerchantAccount(merchantDtuPayId);
		Event publishEvent = new Event(MERCHANT_BANK_ACCOUNT_FOUND, new Object[] {correlationId, merchantAccount});
		queue.publish(publishEvent);
	}
}
