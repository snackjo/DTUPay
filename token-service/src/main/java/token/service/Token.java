package token.service;

import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Value
public class Token {
    String id;
    public static Token generateToken() {
        return new Token(UUID.randomUUID().toString());
    }
    public static List<Token> generateTokens(int tokenAmount) {
        List<Token> tokens = new ArrayList<>();
        for(int i = 0; i < tokenAmount; i++) {
            Token token = generateToken();
            tokens.add(token);
        }
        return tokens;
    }
}
