import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a graph of cities connected by roads.
 * Uses an adjacency matrix to store distances between cities.
 */
public class Graph {
    private static final int INF = Integer.MAX_VALUE / 2; // Half of MAX_VALUE to avoid overflow
    private final List<City> cities; // List of cities in the graph
    private final Map<City, Integer> cityToIndex; // Maps cities to their indices in the matrix
    private int[][] distance; // Adjacency matrix for distances

    /**
     * Constructs an empty graph.
     */
    public Graph() {
        cities = new ArrayList<>();
        cityToIndex = new HashMap<>();
        distance = new int[0][0];
    }

    /**
     * Adds a city to the graph if it doesn't already exist.
     * 
     * @param city the city to add
     * @return the index of the city in the matrix
     */
    public int addCity(City city) {
        if (cityToIndex.containsKey(city)) {
            return cityToIndex.get(city);
        }
        
        int index = cities.size();
        cities.add(city);
        cityToIndex.put(city, index);
        
        // Resize the distance matrix to accommodate the new city
        int newSize = cities.size();
        int[][] newDistance = new int[newSize][newSize];
        
        // Copy existing distances
        for (int i = 0; i < newSize - 1; i++) {
            for (int j = 0; j < newSize - 1; j++) {
                newDistance[i][j] = distance.length > 0 ? distance[i][j] : INF;
            }
        }
        
        // Initialize distances for the new city
        for (int i = 0; i < newSize; i++) {
            if (i != index) {
                newDistance[i][index] = INF;
                newDistance[index][i] = INF;
            }
        }
        
        // Set diagonal to 0 (distance from a city to itself is 0)
        newDistance[index][index] = 0;
        
        distance = newDistance;
        return index;
    }

    /**
     * Adds a road between two cities with the given distance.
     * 
     * @param cityA    the first city
     * @param cityB    the second city
     * @param distance the distance between the cities
     */
    public void addRoad(City cityA, City cityB, int distance) {
        int indexA = addCity(cityA);
        int indexB = addCity(cityB);
        
        this.distance[indexA][indexB] = distance;
        this.distance[indexB][indexA] = distance; // Assuming roads are bidirectional
    }

    /**
     * Computes the shortest paths between all pairs of cities using the Floyd-Warshall algorithm.
     */
    public void computeShortestPaths() {
        int n = cities.size();
        
        // Floyd-Warshall algorithm
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (distance[i][k] < INF && distance[k][j] < INF) {
                        distance[i][j] = Math.min(distance[i][j], distance[i][k] + distance[k][j]);
                    }
                }
            }
        }
    }

    /**
     * Returns the shortest distance between two cities.
     * 
     * @param from the source city
     * @param to   the destination city
     * @return the shortest distance, or INF if there is no path
     */
    public int getShortestDistance(City from, City to) {
        Integer fromIndex = cityToIndex.get(from);
        Integer toIndex = cityToIndex.get(to);
        
        if (fromIndex == null || toIndex == null) {
            return INF;
        }
        
        return distance[fromIndex][toIndex];
    }

    /**
     * Returns the city at the given index.
     * 
     * @param index the index of the city
     * @return the city at the given index
     */
    public City getCity(int index) {
        if (index < 0 || index >= cities.size()) {
            throw new IndexOutOfBoundsException("Invalid city index: " + index);
        }
        return cities.get(index);
    }

    /**
     * Returns the index of the given city.
     * 
     * @param city the city to find the index for
     * @return the index of the city, or -1 if the city is not in the graph
     */
    public int getCityIndex(City city) {
        Integer index = cityToIndex.get(city);
        return index != null ? index : -1;
    }

    /**
     * Returns the number of cities in the graph.
     * 
     * @return the number of cities
     */
    public int getSize() {
        return cities.size();
    }

    /**
     * Returns the list of all cities in the graph.
     * 
     * @return the list of cities
     */
    public List<City> getCities() {
        return new ArrayList<>(cities);
    }
} 