package com.roadtrip.model;

import java.util.Objects;

/**
 * 旅游景点类
 */
public class Attraction {
    private final String name;        // 景点名称
    private final City location;      // 景点所在城市
    
    /**
     * 构造函数
     * @param name 景点名称
     * @param location 景点所在城市
     */
    public Attraction(String name, City location) {
        this.name = name;
        this.location = location;
    }
    
    /**
     * 获取景点名称
     * @return 景点名称
     */
    public String getName() {
        return name;
    }
    
    /**
     * 获取景点所在城市
     * @return 景点所在城市
     */
    public City getLocation() {
        return location;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attraction that = (Attraction) o;
        return Objects.equals(name, that.name) && 
               Objects.equals(location, that.location);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, location);
    }
    
    @Override
    public String toString() {
        return name + " (" + location + ")";
    }
} 