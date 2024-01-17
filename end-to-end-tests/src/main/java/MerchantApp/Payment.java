package MerchantApp;

import lombok.Data;

@Data
public class Payment {
    String merchantDtuPayId;
    Token customerToken;
    int amount;
}
