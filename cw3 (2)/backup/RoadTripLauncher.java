import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * 命令行版本的路线规划器，用于直接从命令行进行路线规划
 */
public class RoadTripLauncher {
    
    /**
     * 主入口点
     */
    public static void main(String[] args) {
        System.out.println("\n===== 路线规划器（命令行版本）=====");
        
        String attractionsFilePath = "CW3_Data_Files/CW3_Data_Files/attractions.csv";
        String roadsFilePath = "CW3_Data_Files/CW3_Data_Files/roads.csv";
        
        RoutePlanner planner = new RoutePlanner(attractionsFilePath, roadsFilePath);
        
        try (Scanner scanner = new Scanner(System.in)) {
            // 显示示例用例
            System.out.println("\n示例测试用例:");
            System.out.println("1. Houston TX → Philadelphia PA (无景点)");
            System.out.println("2. Philadelphia PA → San Antonio TX (途经 Hollywood Sign)");
            System.out.println("3. San Jose CA → Phoenix AZ (途经 Liberty Bell 和 Millennium Park)");
            System.out.println();
            
            // 输入起始城市
            System.out.print("请输入起始城市 (例如: 'New York NY'): ");
            String start = scanner.nextLine().trim();
            
            // 输入目的城市
            System.out.print("请输入目的城市 (例如: 'Los Angeles CA'): ");
            String end = scanner.nextLine().trim();
            
            // 输入景点
            System.out.print("请输入景点（用逗号分隔，不输入则直接按回车）: ");
            String attractionsInput = scanner.nextLine().trim();
            
            List<String> attractions = List.of();
            if (!attractionsInput.isEmpty()) {
                attractions = Arrays.asList(attractionsInput.split("\\s*,\\s*"));
            }
            
            try {
                // 验证城市
                validateCity(start, "起始");
                validateCity(end, "目的");
                
                // 验证景点
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
                    System.out.println("\n警告：以下景点未找到，将被忽略：");
                    for (String invalid : invalidAttractions) {
                        System.out.println("- " + invalid);
                    }
                    
                    if (validAttractions.isEmpty()) {
                        System.out.println("没有有效的景点，将规划直接路线。");
                    } else {
                        System.out.println("\n将使用有效景点: " + validAttractions);
                    }
                    
                    attractions = validAttractions;
                }
                
                // 规划路线
                long startTime = System.currentTimeMillis();
                RouteResult result = planner.route(start, end, attractions);
                long endTime = System.currentTimeMillis();
                
                // 显示结果
                System.out.println("\n----- 路线结果 -----");
                System.out.println("起点: " + start);
                System.out.println("终点: " + end);
                System.out.println("景点: " + attractions);
                System.out.println(result.toString());
                
                // 文字形式显示路径
                System.out.println("\n路径详情：");
                List<City> path = result.getPath();
                for (int i = 0; i < path.size(); i++) {
                    City city = path.get(i);
                    if (i > 0) {
                        System.out.print(" → ");
                    }
                    System.out.print(city.getName() + " " + city.getState());
                }
                System.out.println("\n总距离: " + result.getTotalDistance() + " 英里");
                System.out.println("计算时间: " + (endTime - startTime) + " 毫秒");
                
            } catch (IllegalArgumentException e) {
                System.err.println("错误: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("发生意外错误: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 验证城市字符串格式
     */
    private static void validateCity(String cityStr, String type) throws IllegalArgumentException {
        String[] parts = cityStr.split(" ");
        if (parts.length < 2) {
            throw new IllegalArgumentException(type + "城市必须采用'城市名 州缩写'格式（例如：'New York NY'）");
        }
    }
} 