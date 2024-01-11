package token.service;


import messaging.Event;
import messaging.MessageQueue;

public class TokenService {
    private static final String CUSTOMER_REGISTERED = "CustomerRegistered";
    private static final String TOKENS_REQUESTED = "TokensRequested";
    private static final String TOKENS_GENERATED = "TokensGenerated";
    private final MessageQueue queue;

    public TokenService(MessageQueue q) {
        queue = q;
        queue.addHandler(TOKENS_REQUESTED, this::handleTokensRequested);
        queue.addHandler(CUSTOMER_REGISTERED, this::handleCustomerRegistered);
    }

    public void handleCustomerRegistered(Event ev) {
        Customer customer = ev.getArgument(0, Customer.class);

        CustomerRepository customerRepository = CustomerRepositoryFactory.getRepository();
        customerRepository.addCustomer(customer);
    }

    public void handleTokensRequested(Event event) {
        String dtuPayId = event.getArgument(0, String.class);
        int tokenAmount = event.getArgument(1, Integer.class);
        CorrelationId correlationId = event.getArgument(2, CorrelationId.class);

        CustomerRepository customerRepository = CustomerRepositoryFactory.getRepository();
        customerRepository.addTokensToCustomer(dtuPayId, Token.generateTokens(tokenAmount));

        Event publishedEvent = new Event(TOKENS_GENERATED,
                new Object[] { customerRepository.getCustomer(dtuPayId).getTokens(), correlationId });
        queue.publish(publishedEvent);
    }
}
