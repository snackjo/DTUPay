Feature: Payment service
    Scenario: Successful payment completed
        When a PaymentRequested event is received
        And a CustomerBankAccountFound event is received
        And a MerchantBankAccountFound event is received
        Then a "PaymentCompleted" event is published

    Scenario: Successful payment race condition
        Given that a merchant wants to start a transaction
        And a PaymentRequested event
        And a CustomerBankAccountFound event
        And a MerchantBankAccountFound event
        When they are all received at the same time
        Then only one "PaymentCompleted" event is published

    Scenario: Multiple successful payment race condition
        Given that a merchant wants to start a transaction
        And a PaymentRequested event
        And a CustomerBankAccountFound event
        And a MerchantBankAccountFound event
        Given that another merchant wants to start a transaction
        And another PaymentRequested event
        And another CustomerBankAccountFound event
        And another MerchantBankAccountFound event
        When events from both payments are received at the same time
        Then two "PaymentCompleted" event is published
        And the two events have different correlation id

