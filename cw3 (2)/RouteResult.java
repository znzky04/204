import java.util.ArrayList;
import java.util.List;

/**
 * Represents the result of a route planning operation.
 */
public class RouteResult {
    private final List<City> path;
    private final double totalDistance;

    /**
     * Constructs a RouteResult with the given path and total distance.
     * 
     * @param path          the list of cities in the route
     * @param totalDistance the total distance of the route
     */
    public RouteResult(List<City> path, double totalDistance) {
        this.path = new ArrayList<>(path);
        this.totalDistance = totalDistance;
    }

    /**
     * Returns the path of the route.
     * 
     * @return the list of cities in the route
     */
    public List<City> getPath() {
        return new ArrayList<>(path);
    }

    /**
     * Returns the total distance of the route.
     * 
     * @return the total distance
     */
    public double getTotalDistance() {
        return totalDistance;
    }

    /**
     * Returns a string representation of the route result.
     * 
     * @return a string representation of the route result
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Optimal Route: ").append(path).append("\n");
        sb.append("Total Distance: ").append(totalDistance).append(" miles");
        return sb.toString();
    }
} 