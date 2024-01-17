Feature: Report feature
    Scenario: Manager requests report
        Given a successful payment
        When the manager requests a report
        Then the manager report includes the payment

    Scenario: Customer requests report
        Given a successful payment
        And the payment includes the customer
        When the customer requests a report
        Then the customer report includes the payment

    Scenario: Merchant requests report
        Given a successful payment
        And the payment includes the merchant
        When the merchant requests a report
        Then the merchant report includes the payment