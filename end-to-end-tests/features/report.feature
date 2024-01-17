Feature: Report feature
    Scenario: Manager requests report
        Given a successful payment
        When the manager requests a report
        Then the report includes the payment