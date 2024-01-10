Feature: Token service test
    Scenario: Customer is registered
        When a "CustomerRegistered" event is received
        Then a customer is created with 0 tokens

    Scenario: Generate tokens for customer
        Given a registered customer with 0 tokens
        When a "TokensRequested" event for a customer is received for 3 tokens
        Then a "TokensGenerated" event with 3 tokens is published
        And the customer has 3 tokens