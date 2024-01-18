@author=Carl
Feature: Account service
    Scenario: Account registration for customer
        When a CustomerRegistrationRequested event is received
        Then a "CustomerRegistered" event is published
        And DTUPay id is part of published customer event
        And the customer is given a non-empty DTUPay id

    Scenario: Account registration for merchant
        When a MerchantRegistrationRequested event is received
        Then a "MerchantRegistered" event is published
        And DTUPay id is part of published merchant event
        And the merchant is given a non-empty DTUPay id

    Scenario: Successfully find customer bank account
        Given a registered customer
        When a TokenMatchFound event is received
        Then a "CustomerBankAccountFound" event is published
        And the published customer account id is correct
        And the customer DTUPay id is also in the event

    Scenario: Successfully find merchant bank account
        Given a registered merchant
        When a PaymentRequested event is received
        Then a "MerchantBankAccountFound" event is published
        And the published merchant account id is correct

    Scenario: Deregister account for merchant
        Given a registered merchant
        When a MerchantDeregistrationRequested event is received
        Then a "MerchantDeregistered" event is published
        And the merchant's account is removed

    Scenario: Deregister account for customer
        Given a registered customer
        When a CustomerDeregistrationRequested event is received
        Then a "CustomerDeregistered" event is published
        And the customer's account is removed