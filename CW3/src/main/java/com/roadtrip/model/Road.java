package com.roadtrip.model;

/**
 * 道路类，表示连接两个城市的道路
 */
public class Road {
    private final City cityA;       // 城市A
    private final City cityB;       // 城市B
    private final int distance;     // 两城市间距离（单位：英里）
    
    /**
     * 构造函数
     * @param cityA 城市A
     * @param cityB 城市B
     * @param distance 距离（英里）
     */
    public Road(City cityA, City cityB, int distance) {
        this.cityA = cityA;
        this.cityB = cityB;
        this.distance = distance;
    }
    
    /**
     * 获取城市A
     * @return 城市A
     */
    public City getCityA() {
        return cityA;
    }
    
    /**
     * 获取城市B
     * @return 城市B
     */
    public City getCityB() {
        return cityB;
    }
    
    /**
     * 获取道路距离
     * @return 距离（英里）
     */
    public int getDistance() {
        return distance;
    }
    
    /**
     * 给定一个城市，返回道路另一端的城市
     * @param city 一个城市
     * @return 道路另一端的城市
     * @throws IllegalArgumentException 如果指定的城市不是道路的一端
     */
    public City getOtherCity(City city) {
        if (city.equals(cityA)) {
            return cityB;
        } else if (city.equals(cityB)) {
            return cityA;
        } else {
            throw new IllegalArgumentException("City is not part of this road: " + city);
        }
    }
    
    @Override
    public String toString() {
        return cityA + " <-> " + cityB + " (" + distance + " miles)";
    }
} 