package com.roadtrip.model;

import java.util.Objects;

/**
 * 城市类，表示路网中的一个节点
 */
public class City {
    private final String name;     // 城市名称
    private final String state;    // 州名缩写
    private double latitude;       // 城市纬度
    private double longitude;      // 城市经度
    
    /**
     * 构造函数
     * @param name 城市名称
     * @param state 州名缩写
     */
    public City(String name, String state) {
        this.name = name;
        this.state = state;
        // 默认经纬度设为0
        this.latitude = 0.0;
        this.longitude = 0.0;
    }
    
    /**
     * 带经纬度的构造函数
     * @param name 城市名称
     * @param state 州名缩写
     * @param latitude 纬度
     * @param longitude 经度
     */
    public City(String name, String state, double latitude, double longitude) {
        this.name = name;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    /**
     * 获取城市名称
     * @return 城市名称
     */
    public String getName() {
        return name;
    }
    
    /**
     * 获取州名缩写
     * @return 州名缩写
     */
    public String getState() {
        return state;
    }
    
    /**
     * 获取城市的完整名称（城市名 + 州名）
     * @return 完整城市名
     */
    public String getFullName() {
        return name + " " + state;
    }
    
    /**
     * 获取城市纬度
     * @return 纬度
     */
    public double getLatitude() {
        return latitude;
    }
    
    /**
     * 获取城市经度
     * @return 经度
     */
    public double getLongitude() {
        return longitude;
    }
    
    /**
     * 设置城市纬度
     * @param latitude 纬度
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    
    /**
     * 设置城市经度
     * @param longitude 经度
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City city = (City) o;
        return Objects.equals(name, city.name) && 
               Objects.equals(state, city.state);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, state);
    }
    
    @Override
    public String toString() {
        return name + " " + state;
    }
    
    /**
     * 从城市的完整名称创建城市对象（如 "New York NY"）
     * @param fullName 完整城市名（城市名 + 州名）
     * @return 新的城市对象
     */
    public static City fromFullName(String fullName) {
        String[] parts = fullName.split(" ");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid city format: " + fullName);
        }
        
        String state = parts[parts.length - 1];
        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            if (i > 0) {
                nameBuilder.append(" ");
            }
            nameBuilder.append(parts[i]);
        }
        
        return new City(nameBuilder.toString(), state);
    }
} 