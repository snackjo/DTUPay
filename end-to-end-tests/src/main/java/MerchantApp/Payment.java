package MerchantApp;

import lombok.Data;

@Data
public class Payment {
    Token customerToken;
    int amount;
}
