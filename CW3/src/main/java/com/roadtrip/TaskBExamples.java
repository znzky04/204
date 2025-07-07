package com.roadtrip;

import com.roadtrip.service.RouteService;

import java.io.IOException;
import java.util.List;

/**
 * 任务B示例测试类
 * 运行三个指定的路线规划示例
 */
public class TaskBExamples {
    
    public static void main(String[] args) {
        try {
            RouteService routeService = new RouteService();
            
            // 例1：Houston TX 到 Philadelphia PA，无景点
            System.out.println("\n=== Example 1: Houston TX to Philadelphia PA ===");
            List<String> route1 = routeService.route("Houston TX", "Philadelphia PA", List.of());
            for (String step : route1) {
                System.out.println(step);
            }
            
            // 例2：Philadelphia PA 到 San Antonio TX，经过好莱坞标志
            System.out.println("\n=== Example 2: Philadelphia PA to San Antonio TX via Hollywood Sign ===");
            List<String> route2 = routeService.route("Philadelphia PA", "San Antonio TX", List.of("Hollywood Sign"));
            for (String step : route2) {
                System.out.println(step);
            }
            
            // 例3：San Jose CA 到 Phoenix AZ，经过自由钟和千禧公园
            System.out.println("\n=== Example 3: San Jose CA to Phoenix AZ via Liberty Bell and Millennium Park ===");
            List<String> route3 = routeService.route("San Jose CA", "Phoenix AZ", 
                    List.of("Liberty Bell", "Millennium Park"));
            for (String step : route3) {
                System.out.println(step);
            }
            
        } catch (IOException e) {
            System.err.println("Error: Failed to load data files.");
            e.printStackTrace();
        }
    }
} 