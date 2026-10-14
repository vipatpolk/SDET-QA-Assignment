# Example: Effective Use of Scenario Outlines vs Data Tables

Feature: E-Commerce Order Processing

  # 1. SCENARIO OUTLINE: Used for Data-Driven Testing of identical workflows
  @regression
  Scenario Outline: Calculate product discount based on customer tier
    Given a customer with loyalty tier "<tier>"
    When the customer adds items totaling <order_total> dollars
    Then a discount of <discount_percent> percent should be applied

    Examples:
      | tier     | order_total | discount_percent |
      | Bronze   | 100         | 5                |
      | Silver   | 100         | 10               |
      | Gold     | 100         | 20               |
      | Platinum | 100         | 30               |


  # 2. DATA TABLE: Used for passing structured data into a single step
  @regression
  Scenario: Place an order with multiple line items
    Given the shopping cart contains the following items:
      | Product Name | Quantity | Unit Price |
      | Wireless Mouse | 2      | 25.00      |
      | USB-C Cable    | 3      | 10.00      |
      | Laptop Stand   | 1      | 45.00      |
    When the customer completes the checkout process
    Then the order summary should reflect an item count of 6
    And the total payable amount should be 125.00 dollars
