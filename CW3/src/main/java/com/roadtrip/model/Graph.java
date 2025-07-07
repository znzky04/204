package com.roadtrip.model;

import java.util.*;

/**
 * 图类，表示城市路网
 * 使用邻接表实现无向图
 */
public class Graph {
    // 邻接表：城市 -> 与该城市相连的所有道路
    private final Map<City, List<Road>> adjacencyList;
    
    /**
     * 构造函数，创建空图
     */
    public Graph() {
        this.adjacencyList = new HashMap<>();
    }
    
    /**
     * 向图中添加城市
     * @param city 城市
     */
    public void addCity(City city) {
        if (!adjacencyList.containsKey(city)) {
            adjacencyList.put(city, new ArrayList<>());
        }
    }
    
    /**
     * 向图中添加道路（无向边）
     * @param road 道路
     */
    public void addRoad(Road road) {
        City cityA = road.getCityA();
        City cityB = road.getCityB();
        
        // 确保两端城市都在图中
        addCity(cityA);
        addCity(cityB);
        
        // 将道路添加到两端城市的邻接列表中
        adjacencyList.get(cityA).add(road);
        adjacencyList.get(cityB).add(road);
    }
    
    /**
     * 获取图中所有城市
     * @return 城市集合
     */
    public Set<City> getCities() {
        return Collections.unmodifiableSet(adjacencyList.keySet());
    }
    
    /**
     * 获取与指定城市相连的所有道路
     * @param city 城市
     * @return 道路列表
     */
    public List<Road> getAdjacentRoads(City city) {
        List<Road> roads = adjacencyList.get(city);
        return roads != null ? Collections.unmodifiableList(roads) : Collections.emptyList();
    }
    
    /**
     * 获取与指定城市相邻的所有城市
     * @param city 城市
     * @return 相邻城市列表
     */
    public List<City> getAdjacentCities(City city) {
        List<Road> roads = getAdjacentRoads(city);
        List<City> adjacentCities = new ArrayList<>();
        
        for (Road road : roads) {
            adjacentCities.add(road.getOtherCity(city));
        }
        
        return adjacentCities;
    }
    
    /**
     * 获取两个城市之间的道路
     * @param cityA 城市A
     * @param cityB 城市B
     * @return 连接两城市的道路，如果不存在则返回null
     */
    public Road getRoadBetween(City cityA, City cityB) {
        List<Road> roadsFromA = getAdjacentRoads(cityA);
        
        for (Road road : roadsFromA) {
            if (road.getOtherCity(cityA).equals(cityB)) {
                return road;
            }
        }
        
        return null;
    }
    
    /**
     * 获取两个城市之间的距离
     * @param cityA 城市A的全名
     * @param cityB 城市B的全名
     * @return 两城市之间的距离，如果不直接相连返回-1
     */
    public int getDistance(String cityNameA, String cityNameB) {
        // 查找对应的城市对象
        City cityA = null;
        City cityB = null;
        
        for (City city : getCities()) {
            if (city.toString().equals(cityNameA)) {
                cityA = city;
            } else if (city.toString().equals(cityNameB)) {
                cityB = city;
            }
            
            if (cityA != null && cityB != null) {
                break;
            }
        }
        
        // 如果找不到对应的城市，返回-1
        if (cityA == null || cityB == null) {
            return -1;
        }
        
        // 获取连接两城市的道路
        Road road = getRoadBetween(cityA, cityB);
        
        // 如果道路不存在，返回-1
        if (road == null) {
            return -1;
        }
        
        return road.getDistance();
    }
    
    /**
     * 获取图中所有的边
     * @return 所有道路的列表
     */
    public List<Edge> getAllEdges() {
        List<Edge> edges = new ArrayList<>();
        Set<Road> processedRoads = new HashSet<>();
        
        for (Map.Entry<City, List<Road>> entry : adjacencyList.entrySet()) {
            City city = entry.getKey();
            for (Road road : entry.getValue()) {
                // 避免重复添加同一条道路
                if (!processedRoads.contains(road)) {
                    processedRoads.add(road);
                    City otherCity = road.getOtherCity(city);
                    edges.add(new Edge(city.toString(), otherCity.toString(), road.getDistance()));
                }
            }
        }
        
        return edges;
    }
    
    /**
     * 边类，表示连接两个城市的道路
     */
    public static class Edge {
        private final String city1;
        private final String city2;
        private final int distance;
        
        public Edge(String city1, String city2, int distance) {
            this.city1 = city1;
            this.city2 = city2;
            this.distance = distance;
        }
        
        public String getCity1() {
            return city1;
        }
        
        public String getCity2() {
            return city2;
        }
        
        public int getDistance() {
            return distance;
        }
    }
    
    /**
     * 判断城市是否在图中
     * @param city 城市
     * @return 如果城市在图中则返回true
     */
    public boolean containsCity(City city) {
        return adjacencyList.containsKey(city);
    }
    
    /**
     * 获取图中城市数量
     * @return 城市数量
     */
    public int getCityCount() {
        return adjacencyList.size();
    }
    
    /**
     * 获取图中道路数量
     * @return 道路数量（每条道路只计算一次）
     */
    public int getRoadCount() {
        int totalRoads = 0;
        for (List<Road> roads : adjacencyList.values()) {
            totalRoads += roads.size();
        }
        // 因为每条道路在邻接表中出现两次（两端城市各一次），所以除以2
        return totalRoads / 2;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph with ").append(getCityCount()).append(" cities and ")
          .append(getRoadCount()).append(" roads:\n");
        
        for (City city : adjacencyList.keySet()) {
            sb.append(city).append(" -> ");
            List<City> neighbors = getAdjacentCities(city);
            for (int i = 0; i < neighbors.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                City neighbor = neighbors.get(i);
                Road road = getRoadBetween(city, neighbor);
                sb.append(neighbor).append(" (").append(road.getDistance()).append(" miles)");
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }
} 