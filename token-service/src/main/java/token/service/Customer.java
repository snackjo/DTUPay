package token.service;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Customer {
    private String dtuPayId;
    private List<Token> tokens = new ArrayList<>();

    public void addTokens(List<Token> tokens) {
        this.tokens.addAll(tokens);
    }

    public void removeToken(Token token){
        this.tokens.remove(token);
    }
}
