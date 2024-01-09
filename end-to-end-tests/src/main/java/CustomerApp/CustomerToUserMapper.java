package CustomerApp;

import dtu.ws.fastmoney.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CustomerToUserMapper {

    public User costumerToUser(Customer customer){
        User user = new User();
        user.setCprNumber(customer.getCprNumber());
        user.setFirstName(customer.getFirstName());
        user.setLastName(customer.getLastName());
        return user;
    }
}
