package Utility;

import CustomerApp.Customer;
import MerchantApp.Merchant;
import dtu.ws.fastmoney.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MapperUtility {

    public User costumerToUser(Customer customer){
        User user = new User();
        user.setCprNumber(customer.getCprNumber());
        user.setFirstName(customer.getFirstName());
        user.setLastName(customer.getLastName());
        return user;
    }

    public User merchantToUser(Merchant merchant){
        User user = new User();
        user.setCprNumber(merchant.getCprNumber());
        user.setFirstName(merchant.getFirstName());
        user.setLastName(merchant.getLastName());
        return user;
    }

    public MerchantApp.Token mapCustomerTokenToMerchantToken(CustomerApp.Token customerToken) {
        MerchantApp.Token merchantToken = new MerchantApp.Token();
        merchantToken.setId(customerToken.getId());
        return merchantToken;
    }

    public ManagerApp.Token mapCustomerTokenToManagerToken(CustomerApp.Token customerToken) {
        ManagerApp.Token managerToken = new ManagerApp.Token();
        managerToken.setId(customerToken.getId());
        return managerToken;
    }

    public CustomerApp.Token mapManagerTokenToCustomerToken(ManagerApp.Token managerToken) {
        CustomerApp.Token customerToken = new CustomerApp.Token();
        customerToken.setId(managerToken.getId());
        return customerToken;
    }

    public MerchantApp.Token mapManagerTokenToMerchantToken(ManagerApp.Token managerToken) {
        MerchantApp.Token merchantToken = new MerchantApp.Token();
        merchantToken.setId(managerToken.getId());
        return merchantToken;
    }


    public CustomerApp.Payment mapManagerPaymentToCustomerPayment(ManagerApp.Payment managerPayment){
        CustomerApp.Payment customerPayment = new CustomerApp.Payment();
        customerPayment.setCustomerToken(mapManagerTokenToCustomerToken(managerPayment.getCustomerToken()));
        customerPayment.setAmount(managerPayment.getAmount());
        customerPayment.setCustomerDtuPayId(managerPayment.getCustomerDtuPayId());
        customerPayment.setMerchantDtuPayId(managerPayment.getMerchantDtuPayId());
        return customerPayment;
    }

    public static MerchantApp.Payment mapManagerPaymentToMerchantPayment(ManagerApp.Payment managerPayment) {
        MerchantApp.Payment merchantPayment = new MerchantApp.Payment();
        merchantPayment.setCustomerToken(mapManagerTokenToMerchantToken(managerPayment.getCustomerToken()));
        merchantPayment.setAmount(managerPayment.getAmount());
        merchantPayment.setMerchantDtuPayId(managerPayment.getMerchantDtuPayId());
        return merchantPayment;
    }
}
