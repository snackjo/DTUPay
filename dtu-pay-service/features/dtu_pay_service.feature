Feature: DTUPay service feature
    Scenario: Customer registration
        Given a customer with empty DTUPay id
        When the customer is being registered
        Then a "CustomerRegistrationRequested" event is published
        When a CustomerRegistered event is received
        Then the customer is registered and his DTUPay id is set

    Scenario: Merchant registration
        Given a merchant with empty DTUPay id
        When the merchant is being registered
        Then a "MerchantRegistrationRequested" event is published
        When a MerchantRegistered event is received
        Then the merchant is registered and his DTUPay id is set

    Scenario: Merchant successful payment
        When a payment of 100 is being requested
        Then a "PaymentRequested" event is published
        When a PaymentCompleted event is received
        Then the payment is successful

    Scenario: Customer requests a valid amount of tokens
        When a customer requests 4 tokens
        Then a "TokensRequested" event is published
        When a TokensGenerated event is received
        Then 4 tokens are returned

    Scenario: Customer requests an invalid amount of tokens
        When a customer requests 6 tokens
        Then a "TokensRequested" event is published
        When a TokensRequestRejected event is received
        Then a DTUPay exception is thrown
