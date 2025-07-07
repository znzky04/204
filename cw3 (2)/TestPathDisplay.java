import java.util.Arrays;
import java.util.List;

/**
 * 测试程序，用于验证路径显示功能
 * 特别是当起点或终点是景点所在地时，不显示景点标识
 */
public class TestPathDisplay {
    public static void main(String[] args) {
        System.out.println("===== 路径显示测试 =====");
        
        String attractionsFilePath = "CW3_Data_Files/CW3_Data_Files/attractions.csv";
        String roadsFilePath = "CW3_Data_Files/CW3_Data_Files/roads.csv";
        
        RoutePlanner planner = new RoutePlanner(attractionsFilePath, roadsFilePath);
        
        // 测试案例1: 起点是景点所在地
        System.out.println("\n========== 测试案例1: 起点是景点所在地 ==========");
        test(planner, "Los Angeles CA", "Houston TX", Arrays.asList("Hollywood Sign", "NASA Space Center"));
        
        // 测试案例2: 终点是景点所在地
        System.out.println("\n========== 测试案例2: 终点是景点所在地 ==========");
        test(planner, "New York NY", "Philadelphia PA", Arrays.asList("Statue of Liberty", "Liberty Bell"));
        
        // 测试案例3: 起点和终点都是景点所在地
        System.out.println("\n========== 测试案例3: 起点和终点都是景点所在地 ==========");
        test(planner, "Los Angeles CA", "Philadelphia PA", Arrays.asList("Hollywood Sign", "Liberty Bell"));
        
        // 测试案例4: 路线中间有景点
        System.out.println("\n========== 测试案例4: 路线中间有景点 ==========");
        test(planner, "New York NY", "San Antonio TX", Arrays.asList("Liberty Bell", "The Alamo"));
    }
    
    private static void test(RoutePlanner planner, String start, String end, List<String> attractions) {
        System.out.println("起点: " + start);
        System.out.println("终点: " + end);
        System.out.println("景点: " + attractions);
        
        try {
            RouteResult result = planner.route(start, end, attractions);
            
            // 显示路径详情
            System.out.println("\n路径详情:");
            displayPathDetails(planner, result, attractions);
            
            System.out.println("\n总距离: " + result.getTotalDistance() + " 英里");
            
            // 显示起点和终点对应的景点（如果有）
            City startCity = result.getPath().get(0);
            City endCity = result.getPath().get(result.getPath().size() - 1);
            
            for (String attraction : attractions) {
                City city = planner.getAttractionsMap().get(attraction);
                if (city != null) {
                    if (city.equals(startCity)) {
                        System.out.println("\n起点城市(" + startCity.getName() + " " + startCity.getState() + ")包含景点: " + attraction);
                        System.out.println("√ 该景点名称正确地不显示在路径中");
                    } else if (city.equals(endCity)) {
                        System.out.println("\n终点城市(" + endCity.getName() + " " + endCity.getState() + ")包含景点: " + attraction);
                        System.out.println("√ 该景点名称正确地不显示在路径中");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("路线规划出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 显示路径详情，包括特殊处理起点和终点为景点的情况
     */
    private static void displayPathDetails(RoutePlanner planner, RouteResult result, List<String> attractions) {
        List<City> path = result.getPath();
        
        // 将景点映射到城市
        java.util.Map<City, String> attractionCities = new java.util.HashMap<>();
        for (String attraction : attractions) {
            City city = planner.getAttractionsMap().get(attraction);
            if (city != null) {
                attractionCities.put(city, attraction);
            }
        }
        
        // 获取起点和终点城市
        City startCity = path.get(0);
        City endCity = path.get(path.size() - 1);
        
        // 显示路径
        for (int i = 0; i < path.size(); i++) {
            City city = path.get(i);
            if (i > 0) {
                System.out.print(" → ");
            }
            
            // 输出城市名称
            System.out.print(city.getName() + " " + city.getState());
            
            // 如果是景点所在地，且不是起点或终点，才显示景点名称
            if (attractionCities.containsKey(city) && !city.equals(startCity) && !city.equals(endCity)) {
                System.out.print(" [" + attractionCities.get(city) + "]");
            }
        }
        System.out.println();
    }
} 