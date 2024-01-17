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

}
