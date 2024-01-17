package token.service;


import messaging.Event;
import messaging.MessageQueue;

public class TokenService {
    public static final String CUSTOMER_REGISTERED = "CustomerRegistered";
    public static final String TOKENS_REQUESTED = "TokensRequested";
    public static final String TOKENS_GENERATED = "TokensGenerated";
    public static final String PAYMENT_REQUESTED = "PaymentRequested";
    public static final String TOKEN_MATCH_FOUND = "TokenMatchFound";
    public static final String TOKENS_REQUEST_REJECTED = "TokensRequestRejected";
    public static final String CUSTOMER_DEREGISTERED = "CustomerDeregistered";
    private final MessageQueue queue;
    private final CustomerRepository customerRepository;


    public TokenService(MessageQueue q, CustomerRepository customerRepository) {
        queue = q;
        this.customerRepository = customerRepository;

        queue.addHandler(TOKENS_REQUESTED, this::handleTokensRequested);
        queue.addHandler(CUSTOMER_REGISTERED, this::handleCustomerRegistered);
        queue.addHandler(PAYMENT_REQUESTED, this::handlePaymentRequested);
        queue.addHandler(CUSTOMER_DEREGISTERED, this::handleCustomerDeregistered);
    }

    public void handlePaymentRequested(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        Token token = event.getArgument(2, Token.class);

        String customerDtuPayId = customerRepository.getCustomerByToken(token);
        customerRepository.removeToken(customerDtuPayId, token);

        Event publishedEvent = new Event(TOKEN_MATCH_FOUND,
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

        if(tokenRequestAmount > 5 || tokenRequestAmount < 1) {
            Event publishedEvent = new Event(TOKENS_REQUEST_REJECTED, new Object[] { correlationId });
            queue.publish(publishedEvent);
            return;
        }

        int numberOfTokens = customerRepository.getCustomer(dtuPayId).getTokens().size();
        if(numberOfTokens > 1) {
            Event publishedEvent = new Event(TOKENS_REQUEST_REJECTED, new Object[] { correlationId });
            queue.publish(publishedEvent);
            return;
        }

        customerRepository.addTokensToCustomer(dtuPayId, Token.generateTokens(tokenRequestAmount));
        Event publishedEvent = new Event(TOKENS_GENERATED,
                new Object[] { correlationId, customerRepository.getCustomer(dtuPayId).getTokens() });
        queue.publish(publishedEvent);
    }

    public void handleCustomerDeregistered(Event event) {
        String customerDtuPayId = event.getArgument(1, String.class);
        customerRepository.removeCustomer(customerDtuPayId);
    }
}
