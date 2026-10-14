@ui
Feature: Part 1: UI Automation (Playwright and Java)
  As an e-commerce customer on SauceDemo
  I want to authenticate, explore the product catalog, manage my cart, and complete orders
  So that I have a reliable, high-integrity shopping experience across all supported user personas

  # =========================================================================
  # RULE 1: User Authentication & Access Control
  # =========================================================================
  Rule: User Authentication & Access Control

    @smoke @auth
    Scenario Outline: User authentication across primary account personas
      Given I am on the SauceDemo login page
      When I enter username "<username>" and password "<password>"
      And I click the "Login" button
      Then I should be "<authentication_outcome>"
      And the page should display "<expected_content>"

      Examples:
        | username                | password     | authentication_outcome            | expected_content                          |
        | standard_user           | secret_sauce | granted access to product catalog | Products                                  |
        | locked_out_user         | secret_sauce | denied access due to account lock | ☹️: Sorry, this user has been locked out. |
        | problem_user            | secret_sauce | granted access to product catalog | Products                                  |
        | performance_glitch_user | secret_sauce | granted access after delay        | Products                                  |

    @regression @auth
    Scenario Outline: Authentication validation with missing or invalid credentials
      Given I am on the SauceDemo login page
      When I enter username "<username>" and password "<password>"
      And I click the "Login" button
      Then I should remain on the login page
      And an authentication error banner should display "<error_message>"

      Examples:
        | username        | password     | error_message                                                   |
        |                 |              | ☹️: Username is required                                        |
        | standard_user   |              | ☹️: Password is required                                        |
        | standard_user   | wrong_sauce  | ☹️: Username and password do not match any user in this service |
        | invalid_account | secret_sauce | ☹️: Username and password do not match any user in this service |

    @regression @auth @security
    Scenario Outline: Direct URL access to protected routes without an active session
      Given I am an unauthenticated guest user
      When I attempt to navigate directly to "<protected_page>"
      Then I should be redirected to the login page
      And an authentication error banner should display "☹️: You can only access '/inventory.html' when you are logged in."

      Examples:
        | protected_page                |
        | the inventory catalog page    |
        | the shopping cart page        |
        | the checkout information page |

  # =========================================================================
  # RULE 2: Product Catalog & Item Details Fidelity
  # =========================================================================
  Rule: Product Catalog & Item Details Fidelity

    @regression @catalog
    Scenario Outline: Individual catalog product details baseline verification
      Given I am logged in as "standard_user"
      When I view the product "<item_name>" on the inventory page
      Then the product price should be "<price>"
      And the product description should match "<description>"

      Examples:
        | item_name                 | price  | description                                                                                                                                                            |
        | Sauce Labs Backpack       | $29.99 | Carry All The Things with the sleek, streamlined Sly Pack that melds uncompromising style with unequaled laptop and tablet protection.                                 |
        | Sauce Labs Bike Light     | $9.99  | A red light for your bike that helps you stay seen. It features a water-resistant design with a rubberized mounting band, and includes three light modes.              |
        | Sauce Labs Bolt T-Shirt   | $15.99 | Get your testing superhero on with the Sauce Labs bolt T-shirt. From American Apparel, 100% ringspun combed cotton, heather gray with red bolt.                        |
        | Sauce Labs Fleece Jacket  | $49.99 | It's not every day that you come across a midweight quarter-zip fleece jacket capable of handling everything from a relaxing day outdoors to a busy day at the office. |
        | Sauce Labs Onesie         | $7.99  | Rib snap infant onesie for the junior automation engineer in development. Reinforced 3-snap bottom closure, two-needle hemmed sleeved and bottom won't unravel.        |
        | ☹Sauce Labs T-Shirt (Red) | $15.99 | This classic Sauce Labs t-shirt is perfect to wear when cozying up to your keyboard to automate some tests. Super-soft and comfy with oversized printed tech icon.     |

    @regression @catalog @integrity
    Scenario Outline: Item name and description fidelity across commerce lifecycle stages
      Given I am logged in as "standard_user"
      When I select "<product_name>" for purchase
      Then the product name "<product_name>" and description should match identically at "<commerce_stage>"

      Examples:
        | product_name             | commerce_stage            |
        | Sauce Labs Backpack      | Catalog Inventory View    |
        | Sauce Labs Backpack      | Item Details Subpage      |
        | Sauce Labs Backpack      | Cart Overview Page        |
        | Sauce Labs Backpack      | Checkout Overview Summary |
        | Sauce Labs Fleece Jacket | Catalog Inventory View    |
        | Sauce Labs Fleece Jacket | Item Details Subpage      |
        | Sauce Labs Fleece Jacket | Cart Overview Page        |
        | Sauce Labs Fleece Jacket | Checkout Overview Summary |

    @regression @catalog @sorting
    Scenario Outline: Item name and description alignment during catalog sorting
      Given I am logged in as "standard_user"
      When I select the catalog sort option "<sort_option>"
      Then the first product should be "<first_item>" and the last product should be "<last_item>"
      And each item description across all sorted cards should remain strictly bound to its corresponding product title

      Examples:
        | sort_option         | first_item                | last_item                 |
        | Name (A to Z)       | Sauce Labs Backpack       | ☹Sauce Labs T-Shirt (Red) |
        | Name (Z to A)       | ☹Sauce Labs T-Shirt (Red) | Sauce Labs Backpack       |
        | Price (low to high) | Sauce Labs Onesie         | Sauce Labs Fleece Jacket  |
        | Price (high to low) | Sauce Labs Fleece Jacket  | Sauce Labs Onesie         |

    @regression @catalog @ui-rendering
    Scenario Outline: Item typography, special symbols, and text rendering verification
      Given I am logged in as "standard_user"
      When I inspect the product "<product_name>"
      Then the "<product_attribute>" should render the exact text pattern "<expected_pattern>" without HTML entity escaping artifacts

      Examples:
        | product_name              | product_attribute | expected_pattern          |
        | ☹Sauce Labs T-Shirt (Red) | title             | ☹Sauce Labs T-Shirt (Red) |
        | Sauce Labs Backpack       | description       | Carry All The Things      |
        | Sauce Labs Fleece Jacket  | description       | It's not every day        |
        | Sauce Labs Onesie         | description       | 3-snap bottom closure     |
        | Sauce Labs Onesie         | description       | two-needle hemmed         |

  # =========================================================================
  # RULE 3: Shopping Cart Management & State Dynamics
  # =========================================================================
  Rule: Shopping Cart Management & State Dynamics

    @regression @cart @boundary
    Scenario Outline: Cart counter badge updates and button state transitions
      Given I am logged in as "standard_user"
      When I click the "<button_label>" button for "<item_name>"
      Then the shopping cart badge should display "<expected_badge_count>"
      And the button label for "<item_name>" should change to "<resulting_button_label>"

      Examples:
        | button_label | item_name               | expected_badge_count | resulting_button_label |
        | Add to cart  | Sauce Labs Backpack     | 1                    | Remove                 |
        | Add to cart  | Sauce Labs Bike Light   | 2                    | Remove                 |
        | Add to cart  | Sauce Labs Bolt T-Shirt | 3                    | Remove                 |
        | Remove       | Sauce Labs Bolt T-Shirt | 2                    | Add to cart            |
        | Remove       | Sauce Labs Bike Light   | 1                    | Add to cart            |
        | Remove       | Sauce Labs Backpack     | no badge displayed   | Add to cart            |

    @regression @cart
    Scenario Outline: Item removal from cart overview page with residual item retention
      Given I am logged in as "standard_user"
      And I have added "<item_to_remove>" and "<item_to_retain>" to the cart
      When I navigate to the cart overview page
      And I click the "Remove" button for "<item_to_remove>"
      Then the cart badge should display "<expected_remaining_count>"
      When I click the "Continue Shopping" button
      Then I should be returned to the inventory catalog
      And the button for "<item_to_remove>" should display "Add to cart"
      And the button for "<item_to_retain>" should display "Remove"

      Examples:
        | item_to_remove          | item_to_retain           | expected_remaining_count |
        | Sauce Labs Bolt T-Shirt | Sauce Labs Fleece Jacket | 1                        |
        | Sauce Labs Onesie       | Sauce Labs Backpack      | 1                        |

    @regression @cart @edge-case
    Scenario Outline: Checkout navigation attempt with boundary cart item counts
      Given I am logged in as "standard_user"
      And my shopping cart contains <item_count> items
      When I navigate to the cart overview page
      And I click the "Checkout" button
      Then the system should "<expected_outcome>"

      Examples:
        | item_count | expected_outcome                                            |
        | 0          | allow proceeding to customer information page without items |
        | 1          | advance to customer information page normally               |

  # =========================================================================
  # RULE 4: End-to-End Checkout Flow & Form Validations
  # =========================================================================
  Rule: End-to-End Checkout Flow & Form Validations

    @smoke @checkout
    Scenario Outline: End-to-end checkout happy path across different order configurations
      Given I am logged in as "standard_user"
      And I have added the items "<purchased_items>" to the cart
      When I proceed to checkout
      And I fill customer information with First Name "Jane", Last Name "Doe", and Postal Code "94105"
      And I click the "Continue" button
      Then the item subtotal should be "<subtotal>"
      And the computed tax should be "<tax>"
      And the final order total should be "<total>"
      When I click the "Finish" button
      Then I should be navigated to the checkout complete page
      And the order completion header should display "Thank you for your order!"
      And the cart badge should be cleared

      Examples:
        | purchased_items                                               | subtotal | tax   | total  |
        | Sauce Labs Backpack                                           | $29.99   | $2.40 | $32.39 |
        | Sauce Labs Backpack, Sauce Labs Bike Light, Sauce Labs Onesie | $47.97   | $3.84 | $51.81 |
        | Sauce Labs Fleece Jacket, Sauce Labs Bolt T-Shirt             | $65.98   | $5.28 | $71.26 |

    @regression @checkout @boundary
    Scenario Outline: Checkout Step One mandatory field boundary validations
      Given I am logged in as "standard_user"
      And I have at least 1 item in the cart
      And I am on the checkout step one customer information page
      When I enter First Name "<first_name>", Last Name "<last_name>", and Postal Code "<postal_code>"
      And I click the "Continue" button
      Then I should remain on checkout step one
      And a checkout validation error should display "<expected_error>"

      Examples:
        | first_name | last_name | postal_code | expected_error                 |
        |            | Smith     | 12345       | Error: First Name is required  |
        | John       |           | 12345       | Error: Last Name is required   |
        | John       | Smith     |             | Error: Postal Code is required |

    @regression @checkout @boundary
    Scenario Outline: Checkout input boundary values and payload sanitization
      Given I am logged in as "standard_user"
      And I have at least 1 item in the cart
      And I am on the checkout step one customer information page
      When I enter "<first_name>" in First Name, "<last_name>" in Last Name, and "<postal_code>" in Postal Code
      And I click the "Continue" button
      Then the checkout overview should load without crash or script execution
      And the customer data should be handled securely as literal text

      Examples:
        | first_name                   | last_name                   | postal_code |
        | VeryLongCustomerFirstName255 | VeryLongCustomerLastName255 | 90210-1234  |
        | José-María                   | Niño-Gómez                  | SW1A 1AA    |
        | <script>alert(1)</script>    | <svg/onload=alert(1)>       | 12345       |

    @regression @checkout @state
    Scenario Outline: Browser reload state persistence across checkout phases
      Given I am logged in as "standard_user"
      And I have reached "<checkout_stage>" with 2 items in cart
      When I refresh the browser page
      Then I should remain on "<checkout_stage>"
      And all order attributes and cart items should remain intact

      Examples:
        | checkout_stage            |
        | Checkout Customer Info    |
        | Checkout Overview Summary |

  # =========================================================================
  # RULE 5: UX/UI Navigation, Links & History Persistence
  # =========================================================================
  Rule: UX/UI Navigation, Links & History Persistence

    @regression @nav @ux
    Scenario Outline: Hamburger sidebar navigation drawer link routing
      Given I am logged in as "standard_user"
      When I click the "Open Menu" button
      Then the navigation sidebar drawer should slide into view
      When I click the sidebar navigation link "<link_name>"
      Then the application should navigate to "<destination>"

      Examples:
        | link_name       | destination                         |
        | All Items       | the inventory catalog page          |
        | About           | the external Sauce Labs website     |
        | Logout          | the login page                      |
        | Reset App State | the current page with cleared state |

    @regression @nav @ux @state
    Scenario Outline: Reset App State navigation link cleans in-session cart state
      Given I am logged in as "standard_user"
      And I have added <item_count> items to the shopping cart
      When I open the hamburger sidebar menu
      And I click the "Reset App State" link
      And I close the sidebar menu
      Then the shopping cart counter badge should be removed
      And all catalog item buttons on the inventory page should display "Add to cart"

      Examples:
        | item_count |
        | 1          |
        | 3          |
        | 6          |

    @regression @nav @ux
    Scenario Outline: Multi-step checkout Cancel button route destinations
      Given I am logged in as "standard_user"
      And I have items in the cart
      When I am on "<checkout_stage>"
      And I click the "Cancel" button
      Then I should be returned to "<destination_page>"
      And my cart items should remain preserved

      Examples:
        | checkout_stage            | destination_page       |
        | Checkout Step One Form    | the shopping cart page |
        | Checkout Overview Summary | the inventory catalog  |

    @regression @nav @ux
    Scenario Outline: Return navigation and state preservation between catalog and subpages
      Given I am logged in as "standard_user"
      When I navigate to "<current_subpage>"
      And I click the "<button_label>" button
      Then I should be returned to the inventory catalog page
      And catalog scroll and selection states should be preserved

      Examples:
        | current_subpage                     | button_label      |
        | Cart Overview Page                  | Continue Shopping |
        | Details of Sauce Labs Fleece Jacket | Back to products  |
        | Details of Sauce Labs Backpack      | Back to products  |

    @regression @nav @security
    Scenario Outline: Footer external social media links and tabnabbing security attributes
      Given I am logged in as "standard_user"
      When I inspect the social link for "<platform>"
      Then the link should point to "<destination_website>"
      And the link should open in a new browser tab securely

      Examples:
        | platform | destination_website        |
        | Twitter  | the official Twitter page  |
        | Facebook | the official Facebook page |
        | LinkedIn | the official LinkedIn page |

    @regression @nav @edge-case
    Scenario Outline: Browser Back button navigation across session and completion boundaries
      Given I am logged in as "standard_user"
      When I perform "<session_action>"
      And I click the browser Back button
      Then the application should enforce "<expected_guard>"

      Examples:
        | session_action   | expected_guard                                                           |
        | Logout via menu  | redirection to login page with session access error banner               |
        | Order completion | prevention of duplicate order submission and preservation of 0-item cart |

    @regression @nav @responsive
    Scenario Outline: Responsive layout and touch target accessibility across device viewports
      Given I set the browser viewport to "<device_type>"
      When I navigate to SauceDemo and log in as "standard_user"
      Then the header controls and shopping cart badge should not overlap
      And interactive touch targets should be easily tappable on mobile screens
      When I open the hamburger menu
      Then the sidebar drawer should adapt to the screen dimensions without layout distortion

      Examples:
        | device_type     |
        | Mobile Portrait |
        | Tablet Portrait |

  # =========================================================================
  # RULE 6: Account Persona Variations & Defect Reproduction
  # =========================================================================
  Rule: Account Persona Variations & Defect Reproduction

    @regression @persona @defect
    Scenario Outline: Problem user intentional catalog and functional defect verification
      Given I am logged in as "problem_user"
      When I interact with "<feature_area>"
      Then the system should display the known defect "<manifested_defect>"

      Examples:
        | feature_area                 | manifested_defect                                                      |
        | catalog product images       | all products show the same dog placeholder image                       |
        | product title for Onesie     | navigation incorrectly opens the fleece jacket product details page    |
        | product title for Bike Light | navigation incorrectly opens the fleece jacket product details page    |
        | checkout customer form       | the last name field rejects user input and triggers a validation error |

    @regression @persona @latency
    Scenario Outline: Performance glitch user latency thresholds and flow resilience
      Given I am authenticated as "performance_glitch_user"
      When I perform "<user_action>" with dynamic synchronization waits
      Then the action should complete within a noticeable delay between 4 and 8 seconds
      And the flow should finish successfully with "<expected_outcome>"

      Examples:
        | user_action             | expected_outcome          |
        | logging into the system | Products                  |
        | completing checkout     | Thank you for your order! |
