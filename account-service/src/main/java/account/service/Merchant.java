package account.service;

import lombok.Data;
import lombok.NoArgsConstructor;

// @author Bastian
@NoArgsConstructor
@Data
public class Merchant {
    private String cprNumber;
    private String firstName;
    private String lastName;
    private String accountId;
    private String dtuPayId;
}
