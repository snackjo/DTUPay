package token.service;


import messaging.Event;
import messaging.MessageQueue;

public class TokenService {
    private final MessageQueue queue;
    private final CustomerRepository customerRepository;


    public TokenService(MessageQueue q, CustomerRepository customerRepository) {
        queue = q;
        this.customerRepository = customerRepository;

        queue.addHandler(EventNames.TOKENS_REQUESTED, this::handleTokensRequested);
        queue.addHandler(EventNames.CUSTOMER_REGISTERED, this::handleCustomerRegistered);
        queue.addHandler(EventNames.PAYMENT_REQUESTED, this::handlePaymentRequested);
        queue.addHandler(EventNames.CUSTOMER_DEREGISTERED, this::handleCustomerDeregistered);
    }

    public void handlePaymentRequested(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        Token token = event.getArgument(2, Token.class);

        findTokenMatch(token, correlationId);
    }

    private void findTokenMatch(Token token, CorrelationId correlationId) {
        String customerDtuPayId = customerRepository.getCustomerByToken(token);
        customerRepository.removeToken(customerDtuPayId, token);

        Event publishedEvent = new Event(EventNames.TOKEN_MATCH_FOUND,
                new Object[]{correlationId, customerDtuPayId});
        queue.publish(publishedEvent);
    }

    public void handleCustomerRegistered(Event ev) {
        Customer customer = ev.getArgument(1, Customer.class);

        customerRepository.addCustomer(customer);
    }

    public void handleTokensRequested(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        String dtuPayId = event.getArgument(1, String.class);
        int tokenRequestAmount = event.getArgument(2, Integer.class);

        generateTokens(tokenRequestAmount, correlationId, dtuPayId);
    }

    private void generateTokens(int tokenRequestAmount, CorrelationId correlationId, String dtuPayId) {
        if(tokenRequestAmount > 5 || tokenRequestAmount < 1) {
            Event publishedEvent = new Event(EventNames.TOKENS_REQUEST_REJECTED, new Object[] {correlationId});
            queue.publish(publishedEvent);
            return;
        }

        int numberOfTokens = customerRepository.getCustomer(dtuPayId).getTokens().size();
        if(numberOfTokens > 1) {
            Event publishedEvent = new Event(EventNames.TOKENS_REQUEST_REJECTED, new Object[] {correlationId});
            queue.publish(publishedEvent);
            return;
        }

        customerRepository.addTokensToCustomer(dtuPayId, Token.generateTokens(tokenRequestAmount));
        Event publishedEvent = new Event(EventNames.TOKENS_GENERATED,
                new Object[] {correlationId, customerRepository.getCustomer(dtuPayId).getTokens() });
        queue.publish(publishedEvent);
    }

    public void handleCustomerDeregistered(Event event) {
        String customerDtuPayId = event.getArgument(1, String.class);
        customerRepository.removeCustomer(customerDtuPayId);
    }
}
