import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Plans routes between cities, visiting all specified attractions.
 */
public class RoutePlanner {
    private final Graph graph;
    private final Map<String, City> attractionsMap;
    private final String attractionsFilePath;
    private final String roadsFilePath;
    private static final int TSP_THRESHOLD = 10; // Use TSP for 10 or more attractions

    /**
     * Constructs a RoutePlanner with the specified file paths.
     * 
     * @param attractionsFilePath the path to the attractions CSV file
     * @param roadsFilePath       the path to the roads CSV file
     */
    public RoutePlanner(String attractionsFilePath, String roadsFilePath) {
        this.graph = new Graph();
        this.attractionsMap = new HashMap<>();
        this.attractionsFilePath = attractionsFilePath;
        this.roadsFilePath = roadsFilePath;
        
        try {
            parseAttractionsCSV();
            parseRoadsCSV();
            graph.computeShortestPaths();
        } catch (IOException e) {
            System.err.println("Error initializing RoutePlanner: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Parses the attractions CSV file to populate the attractions map.
     * 
     * @throws IOException if an I/O error occurs
     */
    private void parseAttractionsCSV() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(attractionsFilePath))) {
            // Skip header line
            String line = reader.readLine();
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String attraction = parts[0].trim();
                    String location = parts[1].trim();
                    
                    // Extract city name and state
                    String[] locationParts = location.split(" ");
                    if (locationParts.length >= 2) {
                        String state = locationParts[locationParts.length - 1];
                        StringBuilder cityNameBuilder = new StringBuilder();
                        for (int i = 0; i < locationParts.length - 1; i++) {
                            if (i > 0) {
                                cityNameBuilder.append(" ");
                            }
                            cityNameBuilder.append(locationParts[i]);
                        }
                        String cityName = cityNameBuilder.toString();
                        
                        City city = new City(cityName, state);
                        attractionsMap.put(attraction, city);
                        graph.addCity(city); // Add the city to the graph
                    }
                }
            }
        }
    }

    /**
     * Parses the roads CSV file to populate the graph.
     * 
     * @throws IOException if an I/O error occurs
     */
    private void parseRoadsCSV() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(roadsFilePath))) {
            // Skip header line
            String line = reader.readLine();
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String cityAStr = parts[0].trim();
                    String cityBStr = parts[1].trim();
                    int distance = Integer.parseInt(parts[2].trim());
                    
                    City cityA = parseCityString(cityAStr);
                    City cityB = parseCityString(cityBStr);
                    
                    graph.addRoad(cityA, cityB, distance);
                }
            }
        }
    }

    /**
     * Parses a city string in the format "Name State" into a City object.
     * 
     * @param cityStr the city string
     * @return the City object
     */
    private City parseCityString(String cityStr) {
        String[] parts = cityStr.split(" ");
        if (parts.length >= 2) {
            String state = parts[parts.length - 1];
            StringBuilder cityNameBuilder = new StringBuilder();
            for (int i = 0; i < parts.length - 1; i++) {
                if (i > 0) {
                    cityNameBuilder.append(" ");
                }
                cityNameBuilder.append(parts[i]);
            }
            String cityName = cityNameBuilder.toString();
            return new City(cityName, state);
        }
        throw new IllegalArgumentException("Invalid city string format: " + cityStr);
    }

    /**
     * Plans a route from the start city to the end city, visiting all specified attractions.
     * 
     * @param start       the starting city in the format "Name State"
     * @param end         the ending city in the format "Name State"
     * @param attractions the list of attractions to visit
     * @return the optimal route result
     */
    public RouteResult route(String start, String end, List<String> attractions) {
        List<City> citiesToVisit = new ArrayList<>();
        
        // Add intermediate cities (from attractions)
        for (String attraction : attractions) {
            City city = attractionsMap.get(attraction);
            if (city != null && !citiesToVisit.contains(city)) {
                citiesToVisit.add(city);
            }
        }
        
        // Start and end cities
        City startCity = parseCityString(start);
        City endCity = parseCityString(end);
        
        // If there are no attractions to visit, just return the direct path
        if (citiesToVisit.isEmpty()) {
            List<City> directPath = new ArrayList<>();
            directPath.add(startCity);
            directPath.add(endCity);
            return new RouteResult(directPath, graph.getShortestDistance(startCity, endCity));
        }
        
        // Choose the appropriate algorithm based on the number of attractions
        if (citiesToVisit.size() >= TSP_THRESHOLD) {
            System.out.println("Using TSP algorithm for " + citiesToVisit.size() + " attractions");
            return TSPOptimizer.optimizeRoute(graph, startCity, endCity, citiesToVisit);
        } else {
            // For smaller numbers of attractions, use the permutation-based approach
            return findOptimalRoute(startCity, endCity, citiesToVisit);
        }
    }

    /**
     * Finds the optimal route from start to end, visiting all cities in the citiesToVisit list.
     * Uses a permutation-based approach suitable for small numbers of attractions.
     * 
     * @param start        the starting city
     * @param end          the ending city
     * @param citiesToVisit the cities that must be visited
     * @return the optimal route result
     */
    private RouteResult findOptimalRoute(City start, City end, List<City> citiesToVisit) {
        List<List<City>> permutations = generatePermutations(citiesToVisit);
        
        double minDistance = Double.MAX_VALUE;
        List<City> bestPath = null;
        
        for (List<City> permutation : permutations) {
            double totalDistance = 0;
            List<City> fullPath = new ArrayList<>();
            fullPath.add(start);
            
            // Calculate distance from start to first city
            totalDistance += graph.getShortestDistance(start, permutation.get(0));
            
            // Add intermediate cities
            fullPath.addAll(permutation);
            
            // Calculate distances between intermediate cities
            for (int i = 0; i < permutation.size() - 1; i++) {
                totalDistance += graph.getShortestDistance(permutation.get(i), permutation.get(i + 1));
            }
            
            // Calculate distance from last city to end
            totalDistance += graph.getShortestDistance(permutation.get(permutation.size() - 1), end);
            
            fullPath.add(end);
            
            if (totalDistance < minDistance) {
                minDistance = totalDistance;
                bestPath = fullPath;
            }
        }
        
        return new RouteResult(bestPath, minDistance);
    }

    /**
     * Generates all permutations of the given list of cities.
     * 
     * @param cities the list of cities
     * @return a list of all permutations
     */
    private List<List<City>> generatePermutations(List<City> cities) {
        List<List<City>> result = new ArrayList<>();
        generatePermutationsHelper(cities, 0, result);
        return result;
    }

    /**
     * Helper method for generating permutations.
     * 
     * @param cities the list of cities
     * @param start  the start index
     * @param result the list to store permutations
     */
    private void generatePermutationsHelper(List<City> cities, int start, List<List<City>> result) {
        if (start == cities.size() - 1) {
            result.add(new ArrayList<>(cities));
            return;
        }
        
        for (int i = start; i < cities.size(); i++) {
            // Swap
            Collections.swap(cities, start, i);
            
            // Recursively generate permutations for the rest
            generatePermutationsHelper(cities, start + 1, result);
            
            // Backtrack (swap back)
            Collections.swap(cities, start, i);
        }
    }

    /**
     * Returns the graph used by this route planner.
     * 
     * @return the graph
     */
    public Graph getGraph() {
        return graph;
    }

    /**
     * Returns the map of attractions to cities.
     * 
     * @return the attractions map
     */
    public Map<String, City> getAttractionsMap() {
        return new HashMap<>(attractionsMap);
    }
} 