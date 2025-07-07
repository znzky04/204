package com.roadtrip.algorithm;

import com.roadtrip.model.City;
import com.roadtrip.model.Graph;
import com.roadtrip.model.Road;

import java.util.*;

/**
 * Dijkstra最短路径算法实现
 */
public class DijkstraAlgorithm {
    private final Graph graph;
    
    /**
     * 构造函数
     * @param graph 城市路网图
     */
    public DijkstraAlgorithm(Graph graph) {
        this.graph = graph;
    }
    
    /**
     * 计算从起点到终点的最短路径
     * @param start 起点城市
     * @param end 终点城市
     * @return 最短路径结果
     */
    public ShortestPathResult findShortestPath(City start, City end) {
        // 路径不存在的情况
        if (!graph.containsCity(start) || !graph.containsCity(end)) {
            return null;
        }
        
        // 初始化距离和前驱节点映射
        Map<City, Integer> distances = new HashMap<>();
        Map<City, City> predecessors = new HashMap<>();
        
        // 初始化优先队列
        PriorityQueue<CityDistance> queue = new PriorityQueue<>();
        
        // 初始化所有城市的距离为无穷大
        for (City city : graph.getCities()) {
            distances.put(city, Integer.MAX_VALUE);
        }
        
        // 起点距离设为0
        distances.put(start, 0);
        queue.add(new CityDistance(start, 0));
        
        // Dijkstra算法主循环
        while (!queue.isEmpty()) {
            CityDistance current = queue.poll();
            City currentCity = current.getCity();
            int currentDistance = current.getDistance();
            
            // 如果到达终点，结束搜索
            if (currentCity.equals(end)) {
                break;
            }
            
            // 如果当前距离大于已知最短距离，跳过
            if (currentDistance > distances.get(currentCity)) {
                continue;
            }
            
            // 遍历当前城市的所有邻接城市
            for (Road road : graph.getAdjacentRoads(currentCity)) {
                City neighbor = road.getOtherCity(currentCity);
                int distance = road.getDistance();
                int newDistance = currentDistance + distance;
                
                // 如果找到更短的路径，更新距离
                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    predecessors.put(neighbor, currentCity);
                    queue.add(new CityDistance(neighbor, newDistance));
                }
            }
        }
        
        // 如果终点不可达
        if (distances.get(end) == Integer.MAX_VALUE) {
            return null;
        }
        
        // 重建最短路径
        List<City> path = reconstructPath(start, end, predecessors);
        int totalDistance = distances.get(end);
        
        return new ShortestPathResult(path, totalDistance);
    }
    
    /**
     * 根据前驱节点映射重建最短路径
     * @param start 起点城市
     * @param end 终点城市
     * @param predecessors 前驱节点映射
     * @return 最短路径城市列表
     */
    private List<City> reconstructPath(City start, City end, Map<City, City> predecessors) {
        LinkedList<City> path = new LinkedList<>();
        City current = end;
        
        // 从终点回溯到起点
        while (current != null) {
            path.addFirst(current);
            current = predecessors.get(current);
        }
        
        return path;
    }
    
    /**
     * 城市距离类，用于优先队列排序
     */
    private static class CityDistance implements Comparable<CityDistance> {
        private final City city;
        private final int distance;
        
        public CityDistance(City city, int distance) {
            this.city = city;
            this.distance = distance;
        }
        
        public City getCity() {
            return city;
        }
        
        public int getDistance() {
            return distance;
        }
        
        @Override
        public int compareTo(CityDistance other) {
            return Integer.compare(this.distance, other.distance);
        }
    }
    
    /**
     * 最短路径结果类
     */
    public static class ShortestPathResult {
        private final List<City> path;
        private final int distance;
        
        public ShortestPathResult(List<City> path, int distance) {
            this.path = path;
            this.distance = distance;
        }
        
        public List<City> getPath() {
            return Collections.unmodifiableList(path);
        }
        
        public int getDistance() {
            return distance;
        }
    }
} 