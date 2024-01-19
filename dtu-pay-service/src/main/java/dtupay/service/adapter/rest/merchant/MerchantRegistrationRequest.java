package dtupay.service.adapter.rest.merchant;

import lombok.Data;
import lombok.NoArgsConstructor;

// @author Emil
@NoArgsConstructor
@Data
public class MerchantRegistrationRequest {
    private String cprNumber;
    private String firstName;
    private String lastName;
    private String accountId;
}
