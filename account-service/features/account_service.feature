Feature: Account service
    Scenario: Account registration for customer
        When a "CustomerRegistrationRequested" event for a customer is received
        Then a "CustomerRegistered" event is published
        And DTUPay id is part of published customer event
        And the customer is given a non-empty DTUPay id

    Scenario: Account registration for merchant
        When a "MerchantRegistrationRequested" event for a merchant is received
        Then a "MerchantRegistered" event is published
        And DTUPay id is part of published merchant event
        And the merchant is given a non-empty DTUPay id