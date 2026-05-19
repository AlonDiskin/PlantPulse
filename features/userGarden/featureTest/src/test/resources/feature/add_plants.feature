Feature: User garden plants adding

  # Rule: Find plants for garden

  @search-plant
  Scenario: Plant Searched
    Given user want to add Monstera plant to garden
    When he open plants search screen
    And perform search for this plant
    Then app should list all matching results
    When user perform search for unknown plant
    Then app should list no results, and show "No results found" message