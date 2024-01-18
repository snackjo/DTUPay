package MerchantApp;

import lombok.Data;
import lombok.NoArgsConstructor;

// @author Emil
@NoArgsConstructor
@Data
public class Merchant {
    private String cprNumber;
    private String firstName;
    private String lastName;
    private String accountId;
    private String dtuPayId;
}
