package com.roadtrip.service;

import com.roadtrip.algorithm.RouteOptimizer;
import com.roadtrip.model.Attraction;
import com.roadtrip.model.City;
import com.roadtrip.model.Graph;
import com.roadtrip.util.CSVDataLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 路线服务类，提供路线规划的主要功能接口
 */
public class RouteService {
    private final Graph graph;
    private final Map<String, City> cityMap;
    private final Map<String, Attraction> attractionMap;
    private final RouteOptimizer routeOptimizer;
    
    /**
     * 构造函数，初始化路线服务
     * @throws IOException 如果CSV文件加载失败
     */
    public RouteService() throws IOException {
        // 加载道路图
        this.graph = CSVDataLoader.loadRoadsGraph();
        this.cityMap = CSVDataLoader.createCityMap(graph);
        
        // 加载景点
        List<Attraction> attractions = CSVDataLoader.loadAttractions();
        this.attractionMap = CSVDataLoader.createAttractionMap(attractions);
        
        // 创建路线优化器
        this.routeOptimizer = new RouteOptimizer(graph);
    }
    
    /**
     * 计算路线
     * @param startingCity 起点城市的全名（如 "New York NY"）
     * @param endingCity 终点城市的全名（如 "Chicago IL"）
     * @param attractions 需要访问的景点名称列表
     * @return 路线字符串列表：包含起点、景点和终点的访问顺序，以及总距离
     */
    public List<String> route(String startingCity, String endingCity, List<String> attractions) {
        // 查找起点城市
        City start = cityMap.get(startingCity);
        if (start == null) {
            List<String> result = new ArrayList<>();
            result.add("错误: 起点城市 \"" + startingCity + "\" 不存在。");
            
            // 提供相似城市建议
            List<String> similarCities = findSimilarCities(startingCity);
            if (!similarCities.isEmpty()) {
                result.add("您是否想要输入以下城市之一?");
                for (String city : similarCities) {
                    result.add("- " + city);
                }
            }
            
            result.add("\n可用城市列表:");
            List<String> allCities = new ArrayList<>(cityMap.keySet());
            allCities.sort(String::compareTo);
            for (int i = 0; i < Math.min(10, allCities.size()); i++) {
                result.add("- " + allCities.get(i));
            }
            if (allCities.size() > 10) {
                result.add("...(共 " + allCities.size() + " 个城市)");
            }
            
            return result;
        }
        
        // 查找终点城市
        City end = cityMap.get(endingCity);
        if (end == null) {
            List<String> result = new ArrayList<>();
            result.add("错误: 终点城市 \"" + endingCity + "\" 不存在。");
            
            // 提供相似城市建议
            List<String> similarCities = findSimilarCities(endingCity);
            if (!similarCities.isEmpty()) {
                result.add("您是否想要输入以下城市之一?");
                for (String city : similarCities) {
                    result.add("- " + city);
                }
            }
            
            result.add("\n可用城市列表:");
            List<String> allCities = new ArrayList<>(cityMap.keySet());
            allCities.sort(String::compareTo);
            for (int i = 0; i < Math.min(10, allCities.size()); i++) {
                result.add("- " + allCities.get(i));
            }
            if (allCities.size() > 10) {
                result.add("...(共 " + allCities.size() + " 个城市)");
            }
            
            return result;
        }
        
        // 查找所有景点
        List<Attraction> attractionObjects = new ArrayList<>();
        List<String> invalidAttractions = new ArrayList<>();
        
        for (String attractionName : attractions) {
            Attraction attraction = attractionMap.get(attractionName);
            if (attraction == null) {
                invalidAttractions.add(attractionName);
            } else {
                attractionObjects.add(attraction);
            }
        }
        
        // 如果有无效景点，返回错误信息
        if (!invalidAttractions.isEmpty()) {
            List<String> result = new ArrayList<>();
            result.add("错误: 以下景点名称无效: ");
            for (String invalidAttraction : invalidAttractions) {
                result.add("- \"" + invalidAttraction + "\"");
                
                // 提供相似景点建议
                List<String> similarAttractions = findSimilarAttractions(invalidAttraction);
                if (!similarAttractions.isEmpty()) {
                    result.add("  您是否想要输入以下景点之一?");
                    for (String attraction : similarAttractions) {
                        result.add("  - " + attraction);
                    }
                }
            }
            
            result.add("\n可用景点列表:");
            List<String> allAttractions = new ArrayList<>(attractionMap.keySet());
            allAttractions.sort(String::compareTo);
            for (int i = 0; i < Math.min(10, allAttractions.size()); i++) {
                Attraction attraction = attractionMap.get(allAttractions.get(i));
                result.add("- " + allAttractions.get(i) + " (" + attraction.getLocation() + ")");
            }
            if (allAttractions.size() > 10) {
                result.add("...(共 " + allAttractions.size() + " 个景点)");
            }
            
            return result;
        }
        
        // 计算最优路线
        RouteOptimizer.RouteResult result = routeOptimizer.findOptimalRoute(start, end, attractionObjects);
        
        if (result == null) {
            return List.of("错误: 无法找到有效路线。请检查起点、终点和景点之间是否有可行路径。");
        }
        
        // 生成路线描述
        List<String> route = new ArrayList<>();
        List<City> path = result.getPath();
        
        // 添加起点
        route.add("Starting from: " + start);
        
        // 遍历路径中的所有城市（去掉起点）
        for (int i = 1; i < path.size(); i++) {
            City city = path.get(i);
            
            // 检查是否是景点所在城市
            String description = city.toString();
            for (Attraction attraction : attractionObjects) {
                if (attraction.getLocation().equals(city)) {
                    description += " (Visiting: " + attraction.getName() + ")";
                    break;
                }
            }
            
            route.add(description);
        }
        
        // 添加总距离信息
        route.add("Total distance: " + result.getTotalDistance() + " miles");
        
        return route;
    }
    
    /**
     * 找出与给定城市名称相似的城市
     * @param cityName 输入的城市名称
     * @return 相似城市列表
     */
    private List<String> findSimilarCities(String cityName) {
        // 将输入城市名称转换为小写
        String lowerCityName = cityName.toLowerCase();
        
        return cityMap.keySet().stream()
                .filter(city -> {
                    String lowerCity = city.toLowerCase();
                    
                    // 检查是否包含输入的部分字符串
                    if (lowerCity.contains(lowerCityName) || lowerCityName.contains(lowerCity)) {
                        return true;
                    }
                    
                    // 计算编辑距离，找出相似的城市名称
                    int distance = calculateLevenshteinDistance(lowerCityName, lowerCity);
                    return distance <= 3; // 允许最多3个字符的差异
                })
                .limit(5) // 最多返回5个建议
                .collect(Collectors.toList());
    }
    
    /**
     * 找出与给定景点名称相似的景点
     * @param attractionName 输入的景点名称
     * @return 相似景点列表
     */
    private List<String> findSimilarAttractions(String attractionName) {
        // 将输入景点名称转换为小写
        String lowerAttractionName = attractionName.toLowerCase();
        
        return attractionMap.keySet().stream()
                .filter(attraction -> {
                    String lowerAttraction = attraction.toLowerCase();
                    
                    // 检查是否包含输入的部分字符串
                    if (lowerAttraction.contains(lowerAttractionName) 
                            || lowerAttractionName.contains(lowerAttraction)) {
                        return true;
                    }
                    
                    // 计算编辑距离，找出相似的景点名称
                    int distance = calculateLevenshteinDistance(lowerAttractionName, lowerAttraction);
                    return distance <= 3; // 允许最多3个字符的差异
                })
                .limit(5) // 最多返回5个建议
                .collect(Collectors.toList());
    }
    
    /**
     * 计算两个字符串之间的编辑距离(Levenshtein距离)
     * @param s1 第一个字符串
     * @param s2 第二个字符串
     * @return 编辑距离
     */
    private int calculateLevenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        
        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }
        
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                    Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                );
            }
        }
        
        return dp[s1.length()][s2.length()];
    }
    
    /**
     * 获取路网图
     * @return 路网图
     */
    public Graph getGraph() {
        return graph;
    }
    
    /**
     * 获取城市映射
     * @return 城市名到城市对象的映射
     */
    public Map<String, City> getCityMap() {
        return cityMap;
    }
    
    /**
     * 获取景点映射
     * @return 景点名到景点对象的映射
     */
    public Map<String, Attraction> getAttractionMap() {
        return attractionMap;
    }
} 