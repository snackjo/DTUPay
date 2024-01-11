Feature: Token feature

  Scenario: Successful token generation
    Given customer registered in DTUPay with 0 tokens
    When the customer requests 2 tokens
    Then the customer receives 2 tokens