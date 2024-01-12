Feature: DTUPay service feature
    Scenario: Customer registration
        Given a customer with empty DTUPay id
        When the customer is being registered
        Then a "CustomerRegistrationRequested" event is published
        When a CustomerRegistered event is received
        Then the customer is registered and his DTUPay id is set

    Scenario: Merchant requests payment
        When a payment of 100 is being requested
        Then a "PaymentRequested" event is published
