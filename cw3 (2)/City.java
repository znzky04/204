import java.util.Objects;

/**
 * Represents a city with a name and state.
 */
public class City {
    private final String name;
    private final String state;

    /**
     * Constructs a City with the given name and state.
     * 
     * @param name  the name of the city
     * @param state the state code (e.g., "NY" for New York)
     */
    public City(String name, String state) {
        this.name = name;
        this.state = state;
    }

    /**
     * Returns the name of the city.
     * 
     * @return the name of the city
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the state code.
     * 
     * @return the state code
     */
    public String getState() {
        return state;
    }

    /**
     * Returns a string representation of the city in the format "Name State".
     * 
     * @return a string representation of the city
     */
    @Override
    public String toString() {
        return name + " " + state;
    }

    /**
     * Checks if this city is equal to another object.
     * 
     * @param obj the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        City city = (City) obj;
        return Objects.equals(name, city.name) && Objects.equals(state, city.state);
    }

    /**
     * Returns a hash code value for this city.
     * 
     * @return a hash code value for this city
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, state);
    }
} 