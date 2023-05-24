Feature: Order
  Users must be able to create orders

  Scenario Outline: Create a valid order successfully
    Given User wants to create an order with these items
      | quantity | itemId                               | price |
      | 1        | 15477224-4e33-4c10-9dd3-db1c2234dd92 | 10.50 |
      | 2        | 23ec19ae-e5c1-11ed-a05b-0242ac120003 | 50    |
    And The order has a discount of <discount>
    When User calls the create order API endpoint
    Then User should get a successful response
    And Receive the orderId for this request
    And Save the order successfully
    And Publish an event with this order
    Examples:
      | discount |
      | 0.0      |
      | 11.05    |
      | 8.00     |
      | 10.99    |

  Scenario: Send an order with discount above limit
    Given User wants to create an order
    And The order has a discount of 50
    When User calls the create order API endpoint
    Then User should get a failed response

  Scenario Outline: Send an invalid order discount limit
    Given User wants to create an order with these items
      | quantity | itemId                               | price |
      | 1        | 15477224-4e33-4c10-9dd3-db1c2234dd92 | 10.50 |
      | 2        | 23ec19ae-e5c1-11ed-a05b-0242ac120003 | 50    |
    And The order has a discount of <discount>
    When User calls the create order API endpoint
    Then User should get a failed response
    Examples:
      | discount |
      | 20.0     |
      | 11.06    |
      | 12.00    |
      | 11.99    |


  Scenario: Send an order with no items
    Given User wants to create an order
    And The order don't have any items
    When User calls the create order API endpoint
    Then User should get a failed response