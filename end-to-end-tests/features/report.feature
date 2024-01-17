Feature: Report feature
    Scenario: Manager requests report
        When the manager requests a report
        Then the report is returned
        And it includes the payment