@author=Oliver
Feature: Token service test
    Scenario: Customer is registered
        When a CustomerRegistered event is received
        Then a customer is created with 0 tokens

    Scenario: Generate tokens for customer
        Given a registered customer with 0 tokens
        When a TokensRequested event for a customer is received for 3 tokens
        Then a TokensGenerated event with 3 tokens is published
        And the customer has 3 tokens

    Scenario: Successfully find customer matching token
        Given a registered customer with 1 tokens
        When a PaymentRequested event is received with a token matching the customers
        Then a "TokenMatchFound" event is published
        And the customer's DTUPay id is returned

    Scenario: Customer requests tokens when they have too many
        Given a registered customer with 2 tokens
        When a TokensRequested event for a customer is received for 3 tokens
        Then a "TokensRequestRejected" event is published
        And the customer has 2 tokens

    Scenario: Customer requests too many tokens
        Given a registered customer with 0 tokens
        When a TokensRequested event for a customer is received for 6 tokens
        Then a "TokensRequestRejected" event is published
        And the customer has 0 tokens

    Scenario: Customer requests a negative amount of tokens
        Given a registered customer with 1 tokens
        When a TokensRequested event for a customer is received for -3 tokens
        Then a "TokensRequestRejected" event is published
        And the customer has 1 tokens

    Scenario: Customer requests zero tokens
        Given a registered customer with 1 tokens
        When a TokensRequested event for a customer is received for 0 tokens
        Then a "TokensRequestRejected" event is published
        And the customer has 1 tokens

    Scenario: Customer is deregistered
        Given a registered customer with 2 tokens
        When a CustomerDeregistered event is received
        Then the customer is removed
