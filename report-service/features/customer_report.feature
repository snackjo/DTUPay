Feature: Customer reporting
    Scenario: Show list of payments
        Given customer registered in DTU Pay
        And that customer has no completed payments
        When a PaymentCompleted event is received
        Then that payment is stored
