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

    Scenario: Successfully find customer bank account
        Given a registered customer
        When a "TokenMatchFound" event is received with a matching customer DTUPay id
        Then a "CustomerBankAccountFound" event is published
        And the published customer account id is correct
        And the customer DTUPay id is also in the event

    Scenario: Successfully find merchant bank account
        Given a registered merchant
        When a "PaymentRequested" event is received with a matching merchant DTUPay id
        Then a "MerchantBankAccountFound" event is published
        And the published merchant account id is correct