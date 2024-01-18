package MerchantApp;

import lombok.Data;

// @author Bastian
@Data
public class Payment {
    Token customerToken;
    int amount;
}
