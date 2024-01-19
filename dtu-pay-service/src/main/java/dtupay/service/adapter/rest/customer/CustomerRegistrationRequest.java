package dtupay.service.adapter.rest.customer;

import lombok.Data;
import lombok.NoArgsConstructor;

// @author Bastian
@NoArgsConstructor
@Data
public class CustomerRegistrationRequest {
    private String cprNumber;
    private String firstName;
    private String lastName;
    private String accountId;
}
