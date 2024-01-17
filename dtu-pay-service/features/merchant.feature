Feature: DTUPay service merchant feature
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

    Scenario: Get merchant report for successful payment
        When a merchant requests a report
        Then a "MerchantReportRequested" event is published
        When a MerchantReportGenerated event is received
        Then a merchant report is returned

    Scenario: Merchant deregistration
        When a merchant requests to be deregistered
        Then a "MerchantDeregistrationRequested" event is published
        When a MerchantDeregisteredEvent is received
        Then the merchant deregistration was successful