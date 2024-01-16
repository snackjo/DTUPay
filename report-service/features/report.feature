Feature: Customer reporting
    Scenario: Show list of payments
        Given that no payments have completed yet
        When a PaymentCompleted event is received
        Then that payment is stored
