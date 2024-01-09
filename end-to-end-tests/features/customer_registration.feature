Feature: Customer registration feature
	Scenario: Successful customer registration
      Given customer registered in bank
      When the customer registers with DTUPay
      Then the customer is successfully registered