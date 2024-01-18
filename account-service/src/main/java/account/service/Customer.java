package account.service;

import lombok.Data;
import lombok.NoArgsConstructor;

// @author Oliver
@NoArgsConstructor
@Data
public class Customer {
    private String cprNumber;
    private String firstName;
    private String lastName;
    private String accountId;
    private String dtuPayId;
}
