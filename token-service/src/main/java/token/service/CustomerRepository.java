package token.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// @author Carl
public class CustomerRepository {
    private final Map<String, Customer> customerIdToCustomer = new ConcurrentHashMap<>();
    private final Map<Token, String> tokenToCustomerId = new ConcurrentHashMap<>();

    public void addCustomer(Customer customer) {
        customerIdToCustomer.put(customer.getDtuPayId(), customer);
        for(Token token : customer.getTokens()){
            tokenToCustomerId.put(token, customer.getDtuPayId());
        }
    }

    public void addTokensToCustomer(String customerDtuPayId, List<Token> tokens) {
        customerIdToCustomer.get(customerDtuPayId).addTokens(tokens);
        for(Token token : tokens){
            tokenToCustomerId.put(token, customerDtuPayId);
        }
    }

    public Customer getCustomer(String customerId) {
        return customerIdToCustomer.get(customerId);
    }

    public String getCustomerByToken(Token token){
        return tokenToCustomerId.get(token);
    }

    public void removeToken(String customerDtuPayId, Token token) {
        customerIdToCustomer.get(customerDtuPayId).removeToken(token);
        tokenToCustomerId.remove(token);
    }

    public void removeCustomer(String customerDtuPayId) {
        Customer customer = customerIdToCustomer.remove(customerDtuPayId);
        for (Token token : customer.getTokens()) {
            tokenToCustomerId.remove(token);
        }
    }
}
