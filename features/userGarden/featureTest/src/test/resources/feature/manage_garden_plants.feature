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

  # Rule: Show garden plant detail

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

  # Rule: Search garden plants

  @plant-searched
  Scenario Outline: Garden plant searched
    Given user has plants in garden
    When he open garden search screen
    And perform search for an "<plant_status>" plant
    Then app should return "<search_outcome>"
    When search has result
    And user select to view first result plant detail
    Then app should open plant detail screen to show plant data
    Examples:
      | plant_status | search_outcome |
      | existing     | searched_plant |
      | non_existing | no_results     |

  # Rule: Provide garden plants filters

  @plants-filtered
  Scenario Outline: Garden plants filtered
    Given user has plants in garden
    When he open user garden screen
    And open filters screen
    And apply filters for "<category>" category, "<season>" season, "<sun_care>" sun care, "<water_care>" water care
    Then app should show only those plants that match filters
    Examples:
      | category   | season | sun_care | water_care |
      | herb       | warm   | full     | low        |
      | fruit      | cool   | partial  | high       |



