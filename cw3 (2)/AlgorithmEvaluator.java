import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Evaluates the performance of the route planning algorithm.
 */
public class AlgorithmEvaluator {
    private final RoutePlanner planner;
    private final String smallDataset = "CW3_Data_Files/CW3_Data_Files/1000places_sorted.csv";
    private final String largeDataset = "CW3_Data_Files/CW3_Data_Files/10000places_random.csv";
    
    /**
     * Constructs an AlgorithmEvaluator with the specified route planner.
     * 
     * @param planner the route planner to evaluate
     */
    public AlgorithmEvaluator(RoutePlanner planner) {
        this.planner = planner;
    }
    
    /**
     * Runs a performance test with the specified number of attractions.
     * 
     * @param numAttractions the number of attractions to include in the route
     * @param useSmallDataset whether to use the small dataset
     */
    public void runPerformanceTest(int numAttractions, boolean useSmallDataset) {
        String start = "New York NY";
        String end = "Los Angeles CA";
        
        List<String> attractions = loadRandomAttractions(numAttractions, useSmallDataset);
        
        System.out.println("\n===== Performance Test =====");
        System.out.println("Start: " + start);
        System.out.println("End: " + end);
        System.out.println("Number of attractions: " + numAttractions);
        System.out.println("Dataset: " + (useSmallDataset ? "Small (1000 places)" : "Large (10000 places)"));
        
        long startTime = System.currentTimeMillis();
        RouteResult result;
        
        try {
            result = planner.route(start, end, attractions);
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            System.out.println("\nPerformance Results:");
            System.out.println("Time taken: " + duration + " ms");
            System.out.println("Route distance: " + result.getTotalDistance() + " miles");
            System.out.println("Number of cities in path: " + result.getPath().size());
        } catch (Exception e) {
            System.err.println("Error during performance test: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Loads a list of random attractions from the specified dataset.
     * 
     * @param count          the number of attractions to load
     * @param useSmallDataset whether to use the small dataset
     * @return the list of random attractions
     */
    private List<String> loadRandomAttractions(int count, boolean useSmallDataset) {
        String datasetPath = useSmallDataset ? smallDataset : largeDataset;
        List<String> allAttractions = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(datasetPath))) {
            // Skip header line
            String line = reader.readLine();
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1) {
                    allAttractions.add(parts[0].trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading attractions: " + e.getMessage());
            return List.of();
        }
        
        // Select random attractions
        Random random = new Random(System.currentTimeMillis());
        List<String> selectedAttractions = new ArrayList<>();
        
        // Make sure we don't try to select more attractions than are available
        int availableCount = Math.min(count, allAttractions.size());
        
        for (int i = 0; i < availableCount; i++) {
            int index = random.nextInt(allAttractions.size());
            selectedAttractions.add(allAttractions.get(index));
            allAttractions.remove(index); // Remove to avoid duplicates
        }
        
        return selectedAttractions;
    }
    
    /**
     * The main entry point for algorithm evaluation.
     * 
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        String attractionsFilePath = "CW3_Data_Files/CW3_Data_Files/attractions.csv";
        String roadsFilePath = "CW3_Data_Files/CW3_Data_Files/roads.csv";
        
        RoutePlanner planner = new RoutePlanner(attractionsFilePath, roadsFilePath);
        AlgorithmEvaluator evaluator = new AlgorithmEvaluator(planner);
        
        // Compare algorithm performance with different numbers of attractions
        System.out.println("\n===== Algorithm Evaluation =====");
        
        // Note: As the number of attractions increases, the factorial complexity
        // of generating permutations will cause significant performance degradation
        
        // Small tests (should be fast)
        evaluator.runPerformanceTest(1, true);
        evaluator.runPerformanceTest(2, true);
        evaluator.runPerformanceTest(3, true);
        
        // Medium tests (may take a few seconds)
        evaluator.runPerformanceTest(5, true);
        
        // Large tests (may take much longer)
        // Uncomment to run more intensive tests
        // evaluator.runPerformanceTest(8, true);  // 8! = 40,320 permutations
        // evaluator.runPerformanceTest(10, true); // 10! = 3,628,800 permutations
        
        System.out.println("\nCompleted algorithm evaluation.");
    }
    
    /**
     * Analyzes the time complexity of the algorithm.
     */
    public void analyzeTimeComplexity() {
        System.out.println("\n===== Time Complexity Analysis =====");
        
        System.out.println("Floyd-Warshall Preprocessing:");
        System.out.println("- Time Complexity: O(V³) where V is the number of vertices (cities)");
        System.out.println("- Space Complexity: O(V²) for the distance matrix");
        
        System.out.println("\nPermutation Generation:");
        System.out.println("- Time Complexity: O(k!) where k is the number of attractions");
        System.out.println("- For large values of k, this becomes computationally infeasible");
        
        System.out.println("\nOverall Route Planning:");
        System.out.println("- Time Complexity: O(V³ + k! * k) for preprocessing and route finding");
        System.out.println("- For k ≥ 10, the algorithm becomes very slow due to factorial growth");
        
        System.out.println("\nPossible Improvements:");
        System.out.println("- For large k, use dynamic programming approach for the Traveling Salesman Problem");
        System.out.println("- Time complexity would improve to O(k² * 2^k) which is better than O(k!)");
        System.out.println("- Use heuristic approaches like genetic algorithms for approximate solutions");
    }
} 