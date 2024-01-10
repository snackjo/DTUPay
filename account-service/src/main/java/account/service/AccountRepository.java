package account.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccountRepository {
    private final Map<String, Customer> customerAccountMap = new ConcurrentHashMap<>();
    private final Map<String, Merchant> merchantAccountMap = new ConcurrentHashMap<>();

    public void addCustomer(Customer customer) {
        customerAccountMap.put(customer.getDtuPayId(), customer);
    }

    public String getCustomerAccount(String dtuPayId) {
        return customerAccountMap.get(dtuPayId).getAccountId();
    }

    public void addMerchant(Merchant merchant) {
        merchantAccountMap.put(merchant.getDtuPayId(), merchant);
    }

    public String getMerchantAccount(String dtuPayId) {
        return merchantAccountMap.get(dtuPayId).getAccountId();
    }
}
