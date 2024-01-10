package token.service;


import messaging.Event;
import messaging.MessageQueue;

public class TokenService {
    private static final String TOKENS_REQUESTED = "TokensRequested";
    private static final String TOKENS_GENERATED = "TokensGenerated";
    private final MessageQueue queue;

    public TokenService(MessageQueue q) {
        queue = q;
        queue.addHandler(TOKENS_REQUESTED, this::handleTokensRequested);
    }

    public void handleTokensRequested(Event ev) {
        Customer customer = ev.getArgument(0, Customer.class);
        int tokenAmount = ev.getArgument(1, Integer.class);
        String dtuPayId = customer.getDtuPayId();

        CustomerRepository customerRepository = CustomerRepositoryFactory.getRepository();
        customerRepository.addTokensToCustomer(dtuPayId, Token.generateTokens(tokenAmount));

        Event event = new Event(TOKENS_GENERATED, new Object[] { customerRepository.getCustomer(dtuPayId) });
        queue.publish(event);
    }
}
