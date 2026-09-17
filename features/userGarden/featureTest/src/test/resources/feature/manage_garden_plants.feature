Feature: User garden plants adding

  # Rule: List user garden plants

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

  # Rule: Manage single garden plant

  @show-plant-detail
  Scenario: Plant detail shown
    Given user has plants in garden
    When he open garden screen
    And select to view first plant detail
    Then app should show plant data in plant detail screen

  @plant-deleted
  Scenario: Plant removed from garden
    Given user has plants in garden
    When he open garden screen
    And select to view first plant detail
    Then app should open plant detail screen
    When he select to delete plant via plant detail screen option
    And confirm delete operation
    Then app should remove plant from garden
    And return to garden screen