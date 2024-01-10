Feature: Account service
    Scenario: Account registration for customer
        When a "CustomerRegistrationRequested" event for a customer is received
        Then a "CustomerRegistered" event is published with DTUPay id
        And the customer is given a non-empty DTUPay id