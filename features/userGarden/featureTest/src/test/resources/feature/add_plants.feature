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

  # Rule: Add selected plant

  @add-plant
  Scenario: Plant Added
    Given user want to add Snake Plant to garden
    When he open plants search screen
    And perform search for this plant
    When he select to add plant to his garden from search results
    Then app should add selected plant to his garden collection
    When user open garden screen
    Then show plant in garden screen

  @adding-existing-plant
  Scenario: Existing plant adding denied
    Given user has Monstera Deliciosa plant in garden collection
    When he open garden screen
    And perform search for this plant
    And select to add this plant
    Then app should deny his selection
