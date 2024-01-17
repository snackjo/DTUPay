Feature: DTUPay service customer feature
    Scenario: Customer registration
        Given a customer with empty DTUPay id
        When the customer is being registered
        Then a "CustomerRegistrationRequested" event is published
        When a CustomerRegistered event is received
        Then the customer is registered and his DTUPay id is set

    Scenario: Customer requests a valid amount of tokens
        When a customer requests 4 tokens
        Then a "TokensRequested" event is published
        When a TokensGenerated event is received
        Then 4 tokens are returned

    Scenario: Customer requests an invalid amount of tokens
        When a customer requests 6 tokens
        Then a "TokensRequested" event is published
        When a TokensRequestRejected event is received
        Then a DTUPay exception is thrown

    Scenario: Get customer report for successful payment
        When a customer requests a report
        Then a "CustomerReportRequested" event is published
        When a CustomerReportGenerated event is received
        Then a customer report is returned

    Scenario: Customer deregistration
        When a customer requests to be deregistered
        Then a "CustomerDeregistrationRequested" event is published
        When a CustomerDeregisteredEvent is received
        Then the customer deregistration was successful