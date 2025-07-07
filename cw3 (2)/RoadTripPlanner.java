import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Main application class for planning road trips.
 * This class provides a text-based interface for the road trip planner.
 */
public class RoadTripPlanner {
    
    /**
     * The main entry point for the application.
     * 
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        String attractionsFilePath = "CW3_Data_Files/CW3_Data_Files/attractions.csv";
        String roadsFilePath = "CW3_Data_Files/CW3_Data_Files/roads.csv";
        
        RoutePlanner planner = new RoutePlanner(attractionsFilePath, roadsFilePath);
        
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n===== Road Trip Planner =====");
                System.out.println("1. Plan a trip");
                System.out.println("2. Sorting Algorithm Evaluation");
                System.out.println("3. Exit");
                System.out.print("Enter your choice (1-3): ");
                
                int choice;
                try {
                    choice = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number between 1 and 3.");
                    continue;
                }
                
                switch (choice) {
                    case 1:
                        planTrip(scanner, planner);
                        break;
                    case 2:
                        evaluateSortingAlgorithms();
                        break;
                    case 3:
                        System.out.println("Thank you for using Road Trip Planner. Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 3.");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Prompts the user for trip details and plans a route.
     * 
     * @param scanner the scanner for user input
     * @param planner the route planner
     */
    private static void planTrip(Scanner scanner, RoutePlanner planner) {
        System.out.println("\n----- Plan a Trip -----");
        
        System.out.print("Enter starting city (e.g., 'New York NY'): ");
        String start = scanner.nextLine().trim();
        
        System.out.print("Enter ending city (e.g., 'Los Angeles CA'): ");
        String end = scanner.nextLine().trim();
        
        System.out.print("Enter attractions to visit (comma-separated, leave blank for none): ");
        String attractionsInput = scanner.nextLine().trim();
        
        List<String> attractions = List.of();
        if (!attractionsInput.isEmpty()) {
            attractions = Arrays.asList(attractionsInput.split("\\s*,\\s*"));
            
            // Validate attractions
            List<String> validAttractions = new ArrayList<>();
            List<String> invalidAttractions = new ArrayList<>();
            
            for (String attraction : attractions) {
                if (planner.getAttractionsMap().containsKey(attraction)) {
                    validAttractions.add(attraction);
                } else {
                    invalidAttractions.add(attraction);
                }
            }
            
            if (!invalidAttractions.isEmpty()) {
                System.out.println("\nWarning: The following attractions were not found and will be ignored:");
                for (String invalid : invalidAttractions) {
                    System.out.println("- " + invalid);
                }
                
                if (validAttractions.isEmpty()) {
                    System.out.println("No valid attractions specified. Planning a direct route.");
                } else {
                    System.out.println("\nProceeding with valid attractions: " + validAttractions);
                }
                
                attractions = validAttractions;
            }
        }
        
        try {
            // Validate cities
            validateCity(start, "Starting");
            validateCity(end, "Ending");
            
            // 记录路线规划的开始时间
            long startTime = System.currentTimeMillis();
            
            RouteResult result = planner.route(start, end, attractions);
            
            // 记录路线规划的结束时间
            long endTime = System.currentTimeMillis();
            long computationTime = endTime - startTime;
            
            System.out.println("\n----- Route Result -----");
            System.out.println("Start: " + start);
            System.out.println("End: " + end);
            System.out.println("Attractions: " + attractions);
            System.out.println(result);
            
            // 显示路径详情
            System.out.println("\nPath Details:");
            List<City> path = result.getPath();
            
            // 创建一个景点城市的集合，方便快速查找
            Map<City, String> attractionCities = new HashMap<>();
            for (String attraction : attractions) {
                City city = planner.getAttractionsMap().get(attraction);
                if (city != null) {
                    attractionCities.put(city, attraction);
                }
            }
            
            // 解析起点和终点城市，用于比较
            City startCity = path.get(0);  // 第一个城市就是起点
            City endCity = path.get(path.size() - 1);  // 最后一个城市就是终点
            
            for (int i = 0; i < path.size(); i++) {
                City city = path.get(i);
                if (i > 0) {
                    System.out.print(" → ");
                }
                
                // 输出城市名称
                System.out.print(city.getName() + " " + city.getState());
                
                // 如果这个城市是一个景点所在地，且不是起点或终点，才显示景点名称
                if (attractionCities.containsKey(city) && !city.equals(startCity) && !city.equals(endCity)) {
                    System.out.print(" [" + attractionCities.get(city) + "]");
                }
            }
            
            System.out.println("\nTotal Distance: " + result.getTotalDistance() + " miles");
            System.out.println("Computation Time: " + computationTime + " ms");
            
            // 显示算法信息
            System.out.println("\n----- Algorithm Analysis -----");
            
            // 步骤1：预处理和最短路径计算
            System.out.println("Algorithm Pipeline:");
            System.out.println("1. Data Initialization and Graph Building");
            System.out.println("   - Time Complexity: O(E), where E is the number of roads");
            System.out.println("   - Space Complexity: O(V²), where V is the number of cities");
            
            System.out.println("\n2. Floyd-Warshall Algorithm (All-Pairs Shortest Paths)");
            System.out.println("   - Time Complexity: O(V³)");
            System.out.println("   - Space Complexity: O(V²)");
            
            // 步骤3：路线优化算法
            if (attractions.size() < 10) {
                System.out.println("\n3. Permutation-Based Route Optimization");
                System.out.println("   - Time Complexity: O(k!), where k = " + attractions.size() + " (number of attractions)");
                System.out.println("   - Space Complexity: O(k)");
                System.out.println("   - Algorithm Type: Exhaustive Search");
                System.out.println("   - Guarantees: Optimal Solution");
            } else {
                System.out.println("\n3. Dynamic Programming TSP (Held-Karp Algorithm)");
                System.out.println("   - Time Complexity: O(k² * 2^k), where k = " + attractions.size() + " (number of attractions)");
                System.out.println("   - Space Complexity: O(k * 2^k)");
                System.out.println("   - Algorithm Type: State Compression DP");
                System.out.println("   - Guarantees: Optimal Solution");
            }
            
            // 总体复杂度
            System.out.println("\nOverall Algorithm Complexity:");
            if (attractions.size() < 10) {
                System.out.println("Time: O(V³ + k!), dominated by " + (attractions.size() >= 3 ? "k!" : "V³"));
                System.out.println("Space: O(V² + k), dominated by V²");
            } else {
                System.out.println("Time: O(V³ + k² * 2^k), dominated by k² * 2^k for large k");
                System.out.println("Space: O(V² + k * 2^k), dominated by k * 2^k for large k");
            }
            
        } catch (IllegalArgumentException e) {
            System.err.println("Error planning route: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Validates that a city string has the correct format.
     * 
     * @param cityStr the city string to validate
     * @param type    the type of city (e.g., "Starting", "Ending")
     * @throws IllegalArgumentException if the city string is invalid
     */
    private static void validateCity(String cityStr, String type) throws IllegalArgumentException {
        String[] parts = cityStr.split(" ");
        if (parts.length < 2) {
            throw new IllegalArgumentException(type + " city must be in the format 'City State' (e.g., 'New York NY')");
        }
    }
    
    /**
     * Evaluates the performance of different sorting algorithms on various datasets.
     */
    private static void evaluateSortingAlgorithms() {
        System.out.println("\n===== Sorting Algorithm Evaluation =====");
        
        String[] datasetFiles = {
            "CW3_Data_Files/CW3_Data_Files/1000places_sorted.csv",
            "CW3_Data_Files/CW3_Data_Files/1000places_random.csv",
            "CW3_Data_Files/CW3_Data_Files/10000places_sorted.csv",
            "CW3_Data_Files/CW3_Data_Files/10000places_random.csv"
        };
        
        String[] datasetNames = {
            "1000places_sorted.csv",
            "1000places_random.csv",
            "10000places_sorted.csv",
            "10000places_random.csv"
        };
        
        // 输出表头
        System.out.println("\n| Dataset                  | Insertion Sort (ms) | Quick Sort (ms) | Merge Sort (ms) |");
        System.out.println("|--------------------------|---------------------|-----------------|-----------------|");
        
        // 对每个数据集进行测试
        for (int i = 0; i < datasetFiles.length; i++) {
            String datasetFile = datasetFiles[i];
            String datasetName = datasetNames[i];
            
            // 加载数据集
            List<String> originalData = loadDataset(datasetFile);
            if (originalData.isEmpty()) {
                System.out.println("| " + String.format("%-24s", datasetName) + 
                                  "| Failed to load      | Failed to load   | Failed to load   |");
                continue;
            }
            
            // 测试各个排序算法
            double insertionSortTime = testSortingAlgorithm(originalData, "insertion", 5);
            double quickSortTime = testSortingAlgorithm(originalData, "quick", 5);
            double mergeSortTime = testSortingAlgorithm(originalData, "merge", 5);
            
            // 输出结果
            System.out.println("| " + String.format("%-24s", datasetName) + 
                              "| " + String.format("%-19.2f", insertionSortTime) + 
                              "| " + String.format("%-16.2f", quickSortTime) + 
                              "| " + String.format("%-15.2f", mergeSortTime) + " |");
        }
        
        // 输出复杂度分析
        System.out.println("\n===== Algorithm Complexity Analysis =====");
        System.out.println("Insertion Sort:");
        System.out.println("  - Best Case: O(n) - When data is already sorted");
        System.out.println("  - Average Case: O(n²) - When data is randomly ordered");
        System.out.println("  - Worst Case: O(n²) - When data is reversely sorted");
        System.out.println("  - Space Complexity: O(1) - In-place sorting algorithm");
        
        System.out.println("\nQuick Sort:");
        System.out.println("  - Best Case: O(n log n) - With balanced partitioning");
        System.out.println("  - Average Case: O(n log n) - With random input data");
        System.out.println("  - Worst Case: O(n²) - With already sorted data (poor pivot choice)");
        System.out.println("  - Space Complexity: O(log n) - For recursion stack");
        
        System.out.println("\nMerge Sort:");
        System.out.println("  - Best Case: O(n log n) - Consistent performance");
        System.out.println("  - Average Case: O(n log n) - Consistent performance");
        System.out.println("  - Worst Case: O(n log n) - Consistent performance");
        System.out.println("  - Space Complexity: O(n) - Auxiliary array required");
    }
    
    /**
     * Loads a dataset from a CSV file into a list of strings.
     * 
     * @param filePath the path to the CSV file
     * @return a list of strings containing the data
     */
    private static List<String> loadDataset(String filePath) {
        List<String> data = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                data.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error loading dataset " + filePath + ": " + e.getMessage());
        }
        
        return data;
    }
    
    /**
     * Tests a sorting algorithm on a dataset.
     * 
     * @param originalData the original dataset
     * @param algorithm the algorithm to test ("insertion", "quick", or "merge")
     * @param runs the number of test runs
     * @return the average time in milliseconds
     */
    private static double testSortingAlgorithm(List<String> originalData, String algorithm, int runs) {
        double totalTime = 0;
        
        for (int i = 0; i < runs; i++) {
            // Create a copy of the data for this run
            List<String> data = new ArrayList<>(originalData);
            
            // Run the appropriate sorting algorithm
            long startTime = System.nanoTime();
            
            switch (algorithm.toLowerCase()) {
                case "insertion":
                    insertionSort(data);
                    break;
                case "quick":
                    quickSort(data, 0, data.size() - 1);
                    break;
                case "merge":
                    mergeSort(data, 0, data.size() - 1);
                    break;
            }
            
            long endTime = System.nanoTime();
            double timeInMs = (endTime - startTime) / 1_000_000.0;
            totalTime += timeInMs;
        }
        
        return totalTime / runs;  // Return average time
    }
    
    /**
     * Performs insertion sort on a list of strings.
     * 
     * @param data the list to sort
     */
    private static void insertionSort(List<String> data) {
        for (int i = 1; i < data.size(); i++) {
            String key = data.get(i);
            int j = i - 1;
            
            while (j >= 0 && data.get(j).compareTo(key) > 0) {
                data.set(j + 1, data.get(j));
                j--;
            }
            
            data.set(j + 1, key);
        }
    }
    
    /**
     * Performs quick sort on a list of strings.
     * 
     * @param data the list to sort
     * @param low the starting index
     * @param high the ending index
     */
    private static void quickSort(List<String> data, int low, int high) {
        if (low < high) {
            int pi = partition(data, low, high);
            quickSort(data, low, pi - 1);
            quickSort(data, pi + 1, high);
        }
    }
    
    /**
     * Partitions the list for quick sort.
     * 
     * @param data the list to partition
     * @param low the starting index
     * @param high the ending index
     * @return the partition index
     */
    private static int partition(List<String> data, int low, int high) {
        String pivot = data.get(high);
        int i = low - 1;
        
        for (int j = low; j < high; j++) {
            if (data.get(j).compareTo(pivot) <= 0) {
                i++;
                Collections.swap(data, i, j);
            }
        }
        
        Collections.swap(data, i + 1, high);
        return i + 1;
    }
    
    /**
     * Performs merge sort on a list of strings.
     * 
     * @param data the list to sort
     * @param left the left index
     * @param right the right index
     */
    private static void mergeSort(List<String> data, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            
            mergeSort(data, left, mid);
            mergeSort(data, mid + 1, right);
            
            merge(data, left, mid, right);
        }
    }
    
    /**
     * Merges two sorted sublists.
     * 
     * @param data the list containing the sublists
     * @param left the left index
     * @param mid the middle index
     * @param right the right index
     */
    private static void merge(List<String> data, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;
        
        List<String> L = new ArrayList<>();
        List<String> R = new ArrayList<>();
        
        for (int i = 0; i < n1; i++) {
            L.add(data.get(left + i));
        }
        
        for (int j = 0; j < n2; j++) {
            R.add(data.get(mid + 1 + j));
        }
        
        int i = 0, j = 0, k = left;
        
        while (i < n1 && j < n2) {
            if (L.get(i).compareTo(R.get(j)) <= 0) {
                data.set(k, L.get(i));
                i++;
            } else {
                data.set(k, R.get(j));
                j++;
            }
            k++;
        }
        
        while (i < n1) {
            data.set(k, L.get(i));
            i++;
            k++;
        }
        
        while (j < n2) {
            data.set(k, R.get(j));
            j++;
            k++;
        }
    }
} 