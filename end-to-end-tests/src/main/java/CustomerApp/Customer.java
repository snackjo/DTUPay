package CustomerApp;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class Customer {
    private String cprNumber;
    private String firstName;
    private String lastName;
    private String accountId;
    private String dtuPayId;
    private List<Token> tokens = new ArrayList<>();

    public Token provideToken() throws Exception {
        if(!tokens.isEmpty()) {
            return tokens.get(0);
        }
        throw new Exception("No tokens left");
    }
}
