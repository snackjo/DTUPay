Feature: Payment service
    Scenario: Successful payment completed
        When a PaymentRequested event is received
        And a CustomerBankAccountFound event is received
        And a MerchantBankAccountFound event is received
        Then a "PaymentCompleted" event is published

