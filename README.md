# GitScorer

GitScorer is a Spring Boot application designed to identify and rank popular GitHub repositories based on a custom scoring algorithm. It leverages various metrics such as stars, forks, and the recency of updates to provide a comprehensive popularity score.

## Features

*   **Repository Search:** Find GitHub repositories based on specified criteria.
*   **Custom Scoring:** Ranks repositories using a configurable scoring mechanism that considers:
    *   **Stars:** Number of stars a repository has.
    *   **Forks:** Number of times a repository has been forked.
    *   **Recency of Update:** How recently the repository was updated.
*   **Configurable Weights:** Allows adjustment of weights for each scoring metric to customize the popularity calculation.
*   **RESTful API:** Provides a simple API to query and retrieve ranked repositories.

## Technologies Used

*   **Spring Boot:** Framework for building robust, production-ready applications.
*   **Spring Web:** For building RESTful APIs.
*   **Spring Cloud OpenFeign:** Declarative REST client for easy integration with GitHub API.
*   **Lombok:** Reduces boilerplate code.
*   **Maven:** Dependency management and build automation.
*   **Java 21:** Programming language.
*   **Virtual Threads**: This project uses Virtual Threads.

## Scoring Mechanism

The popularity score for each repository is calculated based on a weighted sum of normalized metrics:

`Score = (NormalizedStars * StarsWeight) + (NormalizedForks * ForksWeight) + (NormalizedRecency * MostRecentUpdateWeight)`

*   **Normalized Stars/Forks:** Uses a logarithmic scale to normalize the star and fork counts, accounting for their power-law distribution.
*   **Normalized Recency:** Uses an exponential decay model, giving higher scores to more recently updated repositories.

## Configuration

You can adjust the weights for each scoring metric in the `src/main/resources/application.yaml` file:

```yaml
weights:
  stars: 1.5
  forks: 1.0
  mostRecentUpdates: 2.0

topNToReturn: 10 (How many of the top repositories to return)
pageLimit: 10 (How many pages to query from Github's API)

```
Modify these values to prioritize certain metrics over others in the scoring calculation.
