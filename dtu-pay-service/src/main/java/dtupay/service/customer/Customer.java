package dtupay.service.customer;

import lombok.Data;
import lombok.NoArgsConstructor;

// @author Peter
@NoArgsConstructor
@Data
public class Customer {
    private String cprNumber;
    private String firstName;
    private String lastName;
    private String accountId;
    private String dtuPayId;
}
