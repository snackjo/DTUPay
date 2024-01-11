package behaviourtests;

import CustomerApp.Customer;
import MerchantApp.Merchant;
import lombok.Data;
@Data
public class StateHolder {
    Customer customer;
    Merchant merchant;
}
