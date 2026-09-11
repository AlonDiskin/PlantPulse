Feature: User garden plants adding

  # Rule: Show user garden plants

  @indicate-empty-garden
  Scenario: Empty Garden Indicated
    Given user has no plants in garden
    When he open garden screen
    Then app should show a ui indication that his garden is empty

  @show-user-garden
  Scenario: User Garden Plants Shown
    Given user has plants in garden
    When he open garden screen
    Then app should show all his garden plants