package token.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomerRepository {
    private final Map<String, Customer> customerIdToCustomer = new ConcurrentHashMap<>();

    public void addCustomer(Customer customer) {
        customerIdToCustomer.put(customer.getDtuPayId(), customer);
    }

    public void addTokensToCustomer(String customerDtuPayId, List<Token> tokens) {
        customerIdToCustomer.get(customerDtuPayId).addTokens(tokens);
    }

    public Customer getCustomer(String customerId) {
        return customerIdToCustomer.get(customerId);
    }
}
