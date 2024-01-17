Feature: Customer reporting
    Scenario: Show list of payments
        Given that no payments have completed yet
        When a PaymentCompleted event is received
        Then that payment is stored

    Scenario: Manager report requested
        Given a completed payment
        When a ManagerReportRequested event is received
        Then a "ManagerReportGenerated" event is published
        And the payment is included

    Scenario: Customer report requested
        Given a completed payment
        When a CustomerReportRequested event is received
        Then a "CustomerReportGenerated" event is published
        And the payment is included


    Scenario: Merchant report requested
        Given a completed payment
        When a MerchantReportRequested event is received
        Then a "MerchantReportGenerated" event is published
        And the payment is included without the customer id