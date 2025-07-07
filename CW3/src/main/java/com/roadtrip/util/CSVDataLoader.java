package com.roadtrip.util;

import com.roadtrip.model.Attraction;
import com.roadtrip.model.City;
import com.roadtrip.model.Graph;
import com.roadtrip.model.Road;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CSV数据加载器，用于读取和解析CSV文件
 */
public class CSVDataLoader {
    
    private static final String ATTRACTIONS_FILE = "Data/attractions.csv";
    private static final String ROADS_FILE = "Data/roads.csv";
    
    /**
     * 加载景点数据
     * @return 景点列表
     * @throws IOException 如果文件读取失败
     */
    public static List<Attraction> loadAttractions() throws IOException {
        return loadAttractions(ATTRACTIONS_FILE);
    }
    
    /**
     * 从指定文件加载景点数据
     * @param filePath 文件路径
     * @return 景点列表
     * @throws IOException 如果文件读取失败
     */
    public static List<Attraction> loadAttractions(String filePath) throws IOException {
        List<Attraction> attractions = new ArrayList<>();
        Path path = Paths.get(filePath);
        
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            // 跳过表头
            String line = reader.readLine();
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String attractionName = parts[0].trim();
                    String locationStr = parts[1].trim();
                    
                    City location = City.fromFullName(locationStr);
                    Attraction attraction = new Attraction(attractionName, location);
                    attractions.add(attraction);
                }
            }
        }
        
        return attractions;
    }
    
    /**
     * 加载道路数据并构建图
     * @return 城市路网图
     * @throws IOException 如果文件读取失败
     */
    public static Graph loadRoadsGraph() throws IOException {
        return loadRoadsGraph(ROADS_FILE);
    }
    
    /**
     * 从指定文件加载道路数据并构建图
     * @param filePath 文件路径
     * @return 城市路网图
     * @throws IOException 如果文件读取失败
     */
    public static Graph loadRoadsGraph(String filePath) throws IOException {
        Graph graph = new Graph();
        Path path = Paths.get(filePath);
        
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            // 跳过表头
            String line = reader.readLine();
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String cityAStr = parts[0].trim();
                    String cityBStr = parts[1].trim();
                    int distance = Integer.parseInt(parts[2].trim());
                    
                    City cityA = City.fromFullName(cityAStr);
                    City cityB = City.fromFullName(cityBStr);
                    Road road = new Road(cityA, cityB, distance);
                    
                    graph.addRoad(road);
                }
            }
        }
        
        return graph;
    }
    
    /**
     * 创建景点名称到景点对象的映射
     * @param attractions 景点列表
     * @return 景点名称 -> 景点对象 的映射
     */
    public static Map<String, Attraction> createAttractionMap(List<Attraction> attractions) {
        Map<String, Attraction> attractionMap = new HashMap<>();
        for (Attraction attraction : attractions) {
            attractionMap.put(attraction.getName(), attraction);
        }
        return attractionMap;
    }
    
    /**
     * 创建城市全名到城市对象的映射
     * @param graph 路网图
     * @return 城市全名 -> 城市对象 的映射
     */
    public static Map<String, City> createCityMap(Graph graph) {
        Map<String, City> cityMap = new HashMap<>();
        for (City city : graph.getCities()) {
            cityMap.put(city.getFullName(), city);
        }
        return cityMap;
    }
    
    /**
     * 加载排序测试数据
     * @param filePath 文件路径
     * @return 字符串列表
     * @throws IOException 如果文件读取失败
     */
    public static List<String> loadSortingData(String filePath) throws IOException {
        List<String> data = new ArrayList<>();
        Path path = Paths.get(filePath);
        
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    data.add(line.trim());
                }
            }
        }
        
        return data;
    }
} 