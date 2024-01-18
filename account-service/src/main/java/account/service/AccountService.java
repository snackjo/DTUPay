package account.service;

import messaging.Event;
import messaging.MessageQueue;

import java.util.UUID;

// @author Bastian
public class AccountService {

    private final MessageQueue queue;
    private final AccountRepository accountRepository;

    public AccountService(MessageQueue q, AccountRepository accountRepository) {
        this.queue = q;
        this.accountRepository = accountRepository;

        this.queue.addHandler(EventNames.CUSTOMER_REGISTRATION_REQUESTED, this::handleCustomerRegistrationRequested);
        this.queue.addHandler(EventNames.MERCHANT_REGISTRATION_REQUESTED, this::handleMerchantRegistrationRequested);
        this.queue.addHandler(EventNames.TOKEN_MATCH_FOUND, this::handleTokenMatchFound);
        this.queue.addHandler(EventNames.PAYMENT_REQUESTED, this::handlePaymentRequested);
        this.queue.addHandler(EventNames.MERCHANT_DEREGISTRATION_REQUESTED, this::handleMerchantDeregistrationRequested);
        this.queue.addHandler(EventNames.CUSTOMER_DEREGISTRATION_REQUESTED, this::handleCustomerDeregistrationRequested);
    }

    public void handleCustomerRegistrationRequested(Event ev) {
        CorrelationId correlationId = ev.getArgument(0, CorrelationId.class);
        Customer customer = ev.getArgument(1, Customer.class);

        registerCustomer(customer, correlationId);
    }

    private void registerCustomer(Customer customer, CorrelationId correlationId) {
        customer.setDtuPayId(UUID.randomUUID().toString());
        accountRepository.addCustomer(customer);

        Event event = new Event(EventNames.CUSTOMER_REGISTERED, new Object[]{correlationId, customer});
        queue.publish(event);
    }

    public void handleMerchantRegistrationRequested(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        Merchant merchant = event.getArgument(1, Merchant.class);

        registerMerchant(merchant, correlationId);
    }

    private void registerMerchant(Merchant merchant, CorrelationId correlationId) {
        merchant.setDtuPayId(UUID.randomUUID().toString());
        accountRepository.addMerchant(merchant);

        Event publishEvent = new Event(EventNames.MERCHANT_REGISTERED, new Object[]{correlationId, merchant});
        queue.publish(publishEvent);
    }

    public void handleTokenMatchFound(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        String customerDtuPayId = event.getArgument(1, String.class);

        findCustomerBankAccount(customerDtuPayId, correlationId);
    }

    private void findCustomerBankAccount(String customerDtuPayId, CorrelationId correlationId) {
        String customerAccount;
        try {
            customerAccount = accountRepository.getCustomerAccount(customerDtuPayId);
        } catch (DtuPayException e) {
            throw new RuntimeException(e);
        }
        Event publishEvent = new Event(EventNames.CUSTOMER_BANK_ACCOUNT_FOUND, new Object[]{correlationId, customerAccount, customerDtuPayId});
        queue.publish(publishEvent);
    }

    public void handlePaymentRequested(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        String merchantDtuPayId = event.getArgument(1, String.class);

        findMerchantBankAccount(merchantDtuPayId, correlationId);
    }

    private void findMerchantBankAccount(String merchantDtuPayId, CorrelationId correlationId) {
        String merchantAccount;
        try {
            merchantAccount = accountRepository.getMerchantAccount(merchantDtuPayId);
        } catch (DtuPayException e) {
            throw new RuntimeException(e);
        }
        Event publishEvent = new Event(EventNames.MERCHANT_BANK_ACCOUNT_FOUND, new Object[]{correlationId, merchantAccount});
        queue.publish(publishEvent);
    }

    public void handleMerchantDeregistrationRequested(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        String merchantDtuPayId = event.getArgument(1, String.class);

        deregisterMerchant(merchantDtuPayId, correlationId);
    }

    private void deregisterMerchant(String merchantDtuPayId, CorrelationId correlationId) {
        accountRepository.removeMerchant(merchantDtuPayId);

        Event publishEvent = new Event(EventNames.MERCHANT_DEREGISTERED, new Object[]{correlationId});
        queue.publish(publishEvent);
    }

    public void handleCustomerDeregistrationRequested(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        String customerDtuPayId = event.getArgument(1, String.class);

        deregisterCustomer(customerDtuPayId, correlationId);
    }

    private void deregisterCustomer(String customerDtuPayId, CorrelationId correlationId) {
        accountRepository.removeCustomer(customerDtuPayId);

        Event publishEvent = new Event(EventNames.CUSTOMER_DEREGISTERED, new Object[]{correlationId, customerDtuPayId});
        queue.publish(publishEvent);
    }
}
