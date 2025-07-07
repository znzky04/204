# Road Trip Planner

A command-line application for planning road trips, finding optimal routes, and evaluating sorting algorithms.

## Overview

This application allows you to:
1. Plan a road trip by specifying start and end cities, plus attractions to visit along the way
2. Evaluate the performance of different sorting algorithms (Insertion Sort, Quick Sort, Merge Sort)

## Files

- `RoadTripPlanner.java` - Main application with text-based interface
- `RoutePlanner.java` - Core route planning logic
- `TSPOptimizer.java` - Traveling salesman problem optimization for routes with many attractions
- `Graph.java` - Graph representation for the road network
- `City.java` - City data structure
- `RouteResult.java` - Route result data structure
- `AlgorithmEvaluator.java` - Evaluates sorting algorithm performance

## Data Files

The application uses data files stored in the `CW3_Data_Files/CW3_Data_Files/` directory:
- `attractions.csv` - List of attractions and their locations
- `roads.csv` - Road connections between cities
- Sorting test files: `1000places_sorted.csv`, `1000places_random.csv`, `10000places_sorted.csv`, `10000places_random.csv`

## How to Run

### Using the Batch File
1. Double-click the `run_roadtrip.bat` file.

### Manual Compilation and Execution
```
javac RoadTripPlanner.java
java RoadTripPlanner
```

## Usage

### Planning a Trip
1. Select option 1 from the main menu
2. Enter the starting city (e.g., "New York NY")
3. Enter the ending city (e.g., "Los Angeles CA")
4. Enter attractions to visit, separated by commas (e.g., "Grand Canyon, Las Vegas Strip")

### Evaluating Sorting Algorithms
1. Select option 2 from the main menu
2. The application will test different sorting algorithms on various datasets and display the results

## Implementation Details

### Route Planning Algorithm
- For small numbers of attractions (<10), the application uses a permutation-based approach to find the optimal route
- For 10 or more attractions, it uses a dynamic programming approach based on the Traveling Salesman Problem (TSP)

### Sorting Algorithms
- Insertion Sort - O(n²) time complexity, O(1) space complexity
- Quick Sort - O(n log n) average time complexity, O(log n) space complexity
- Merge Sort - O(n log n) time complexity, O(n) space complexity 