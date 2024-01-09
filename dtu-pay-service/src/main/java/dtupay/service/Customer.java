package dtupay.service;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Customer {
    private String cprNumber;
    private String firstName;
    private String lastName;
    private String accountId;
    private String dtuPayId;
}
