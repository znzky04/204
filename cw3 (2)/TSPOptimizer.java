import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Optimizes routes using a dynamic programming approach to the Traveling Salesman Problem (TSP).
 * This is more efficient than permutation generation for larger numbers of attractions.
 */
public class TSPOptimizer {
    private final Graph graph;
    private final int[][] dp; // DP array: dp[i][j] = shortest path visiting subset i ending at node j
    private final int[][] next; // Next array to reconstruct the path
    private final int n; // Number of nodes
    private final int INF = Integer.MAX_VALUE / 2;
    
    /**
     * Constructs a TSPOptimizer for the given cities.
     * 
     * @param graph      the graph of cities
     * @param citiesToVisit the list of cities to visit (including start and end)
     */
    public TSPOptimizer(Graph graph, List<City> allCities) {
        this.graph = graph;
        this.n = allCities.size();
        
        // Initialize DP array and next array
        int numSubsets = 1 << n; // 2^n possible subsets
        dp = new int[numSubsets][n];
        next = new int[numSubsets][n];
        
        // Initialize with infinity
        for (int i = 0; i < numSubsets; i++) {
            Arrays.fill(dp[i], INF);
            Arrays.fill(next[i], -1);
        }
    }
    
    /**
     * Solves the TSP for the given cities.
     * 
     * @param startIndex the index of the starting city
     * @param endIndex   the index of the ending city
     * @param cityIndices the indices of the cities in the graph
     * @return the optimal route result
     */
    public RouteResult solve(int startIndex, int endIndex, int[] cityIndices) {
        // Base case: starting at the start node
        dp[1 << startIndex][startIndex] = 0;
        
        // For each subset of nodes
        for (int mask = 0; mask < (1 << n); mask++) {
            // Skip if the subset doesn't include the start node
            if ((mask & (1 << startIndex)) == 0) continue;
            
            // For each possible last node in the current subset
            for (int u = 0; u < n; u++) {
                // Skip if the node u is not in the subset
                if ((mask & (1 << u)) == 0) continue;
                
                // For each possible next node v
                for (int v = 0; v < n; v++) {
                    // Skip if v is already in the subset
                    if ((mask & (1 << v)) != 0) continue;
                    
                    // Calculate new mask that includes node v
                    int newMask = mask | (1 << v);
                    
                    // Calculate distance from city u to city v in the original graph
                    int distance = graph.getShortestDistance(
                            graph.getCity(cityIndices[u]), 
                            graph.getCity(cityIndices[v])
                    );
                    
                    // Update if we found a shorter path
                    if (dp[mask][u] + distance < dp[newMask][v]) {
                        dp[newMask][v] = dp[mask][u] + distance;
                        next[newMask][v] = u;
                    }
                }
            }
        }
        
        // Find the shortest path that ends at the end node and visits all nodes
        int fullMask = (1 << n) - 1;
        
        // Extract the optimal path
        List<City> optimalPath = reconstructPath(startIndex, endIndex, fullMask, cityIndices);
        int totalDistance = dp[fullMask][endIndex];
        
        return new RouteResult(optimalPath, totalDistance);
    }
    
    /**
     * Reconstructs the optimal path from the DP and next arrays.
     * 
     * @param startIndex the index of the starting city
     * @param endIndex   the index of the ending city
     * @param mask       the subset mask representing all cities visited
     * @param cityIndices the indices of the cities in the graph
     * @return the list of cities in the optimal path
     */
    private List<City> reconstructPath(int startIndex, int endIndex, int mask, int[] cityIndices) {
        List<Integer> pathIndices = new ArrayList<>();
        
        // Start from the end node
        int currentNode = endIndex;
        pathIndices.add(currentNode);
        
        // Reconstruct the path backwards
        while (currentNode != startIndex) {
            int prevNode = next[mask][currentNode];
            mask = mask & ~(1 << currentNode); // Remove current node from the mask
            currentNode = prevNode;
            pathIndices.add(0, currentNode);
        }
        
        // Convert path indices to cities
        List<City> path = new ArrayList<>();
        for (int idx : pathIndices) {
            path.add(graph.getCity(cityIndices[idx]));
        }
        
        return path;
    }
    
    /**
     * Optimizes the route using dynamic programming for the TSP.
     * 
     * @param start        the starting city
     * @param end          the ending city
     * @param citiesToVisit the list of cities to visit (excluding start and end)
     * @return the optimal route result
     */
    public static RouteResult optimizeRoute(Graph graph, City start, City end, List<City> citiesToVisit) {
        // Create a list of all cities to visit (including start and end)
        List<City> allCities = new ArrayList<>();
        allCities.add(start);
        allCities.addAll(citiesToVisit);
        allCities.add(end);
        
        // Create a mapping of cities to their local indices
        int n = allCities.size();
        int[] cityIndices = new int[n];
        for (int i = 0; i < n; i++) {
            cityIndices[i] = graph.getCityIndex(allCities.get(i));
        }
        
        // Solve the TSP
        TSPOptimizer optimizer = new TSPOptimizer(graph, allCities);
        return optimizer.solve(0, n - 1, cityIndices);
    }
} 