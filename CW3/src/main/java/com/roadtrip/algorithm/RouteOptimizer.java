package com.roadtrip.algorithm;

import com.roadtrip.model.Attraction;
import com.roadtrip.model.City;
import com.roadtrip.model.Graph;

import java.util.*;

/**
 * 路线优化器，用于计算访问多个景点的最短路径
 */
public class RouteOptimizer {
    private final Graph graph;
    private final DijkstraAlgorithm dijkstra;
    
    /**
     * 构造函数
     * @param graph 城市路网图
     */
    public RouteOptimizer(Graph graph) {
        this.graph = graph;
        this.dijkstra = new DijkstraAlgorithm(graph);
    }
    
    /**
     * 计算从起点出发，访问所有景点后到达终点的最短路径
     * 使用贪心算法：每次选择距离当前位置最近的下一个景点
     * 
     * @param startCity 起点城市
     * @param endCity 终点城市
     * @param attractions 需要访问的景点列表
     * @return 路线结果
     */
    public RouteResult findOptimalRoute(City startCity, City endCity, List<Attraction> attractions) {
        // 如果起点或终点不在图中，返回null
        if (!graph.containsCity(startCity) || !graph.containsCity(endCity)) {
            return null;
        }
        
        // 如果没有景点需要访问，直接计算起点到终点的最短路径
        if (attractions.isEmpty()) {
            DijkstraAlgorithm.ShortestPathResult result = dijkstra.findShortestPath(startCity, endCity);
            return result == null ? null : new RouteResult(result.getPath(), result.getDistance());
        }
        
        // 城市到城市的距离缓存，避免重复计算
        Map<String, DijkstraAlgorithm.ShortestPathResult> distanceCache = new HashMap<>();
        
        // 将所有景点转换为景点所在城市
        List<City> attractionCities = new ArrayList<>();
        for (Attraction attraction : attractions) {
            attractionCities.add(attraction.getLocation());
        }
        
        // 当前位置，初始为起点
        City currentCity = startCity;
        
        // 记录累计距离和完整路径
        int totalDistance = 0;
        List<City> fullPath = new ArrayList<>();
        
        // 记录已访问的城市
        Set<City> visitedCities = new HashSet<>();
        
        // 遍历所有景点，每次选择距离当前位置最近的景点
        while (!attractionCities.isEmpty()) {
            // 找出下一个最近的景点城市
            City nextCity = null;
            DijkstraAlgorithm.ShortestPathResult shortestPathToNext = null;
            int shortestDistance = Integer.MAX_VALUE;
            
            for (City attractionCity : attractionCities) {
                // 如果已经访问过此城市，跳过
                if (visitedCities.contains(attractionCity)) {
                    continue;
                }
                
                // 计算当前城市到景点城市的最短路径
                String cacheKey = currentCity.toString() + "->" + attractionCity.toString();
                DijkstraAlgorithm.ShortestPathResult pathResult = distanceCache.get(cacheKey);
                
                if (pathResult == null) {
                    pathResult = dijkstra.findShortestPath(currentCity, attractionCity);
                    distanceCache.put(cacheKey, pathResult);
                }
                
                // 如果无法到达此景点城市，跳过
                if (pathResult == null) {
                    continue;
                }
                
                // 如果此路径更短，更新下一个目标城市
                if (pathResult.getDistance() < shortestDistance) {
                    nextCity = attractionCity;
                    shortestPathToNext = pathResult;
                    shortestDistance = pathResult.getDistance();
                }
            }
            
            // 如果找不到下一个可访问的景点城市，路径不可行
            if (nextCity == null) {
                return null;
            }
            
            // 添加到当前城市到下一个景点城市的路径
            List<City> pathSegment = shortestPathToNext.getPath();
            
            // 如果不是第一段路径，移除重复的当前城市（路径段的第一个城市）
            if (!fullPath.isEmpty()) {
                pathSegment = pathSegment.subList(1, pathSegment.size());
            }
            
            fullPath.addAll(pathSegment);
            totalDistance += shortestPathToNext.getDistance();
            
            // 更新当前位置和已访问集合
            currentCity = nextCity;
            visitedCities.add(currentCity);
            attractionCities.remove(currentCity);
        }
        
        // 最后计算从最后一个景点到终点的路径
        DijkstraAlgorithm.ShortestPathResult finalPathResult = dijkstra.findShortestPath(currentCity, endCity);
        
        // 如果无法到达终点，路径不可行
        if (finalPathResult == null) {
            return null;
        }
        
        // 添加最后一段路径（不包括重复的当前城市）
        List<City> finalPathSegment = finalPathResult.getPath().subList(1, finalPathResult.getPath().size());
        fullPath.addAll(finalPathSegment);
        totalDistance += finalPathResult.getDistance();
        
        return new RouteResult(fullPath, totalDistance);
    }
    
    /**
     * 路线结果类
     */
    public static class RouteResult {
        private final List<City> path;
        private final int totalDistance;
        
        public RouteResult(List<City> path, int totalDistance) {
            this.path = Collections.unmodifiableList(new ArrayList<>(path));
            this.totalDistance = totalDistance;
        }
        
        public List<City> getPath() {
            return path;
        }
        
        public int getTotalDistance() {
            return totalDistance;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Route (").append(totalDistance).append(" miles):\n");
            
            for (int i = 0; i < path.size(); i++) {
                sb.append(i + 1).append(". ").append(path.get(i));
                if (i < path.size() - 1) {
                    sb.append("\n");
                }
            }
            
            return sb.toString();
        }
    }
} 