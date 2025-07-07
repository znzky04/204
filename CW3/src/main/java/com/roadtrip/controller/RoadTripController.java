package com.roadtrip.controller;

import com.roadtrip.model.Attraction;
import com.roadtrip.service.RouteService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class RoadTripController implements Initializable {

    @FXML
    private ComboBox<String> startCityComboBox;

    @FXML
    private ComboBox<String> endCityComboBox;

    @FXML
    private ComboBox<String> attractionComboBox;

    @FXML
    private Button addAttractionButton;

    @FXML
    private Button removeAttractionButton;

    @FXML
    private Button calculateRouteButton;

    @FXML
    private ListView<String> selectedAttractionsListView;

    @FXML
    private ListView<String> routeDetailsListView;

    @FXML
    private Canvas mapCanvas;

    private RouteService routeService;
    private ObservableList<String> selectedAttractions = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // 初始化路线服务
            routeService = new RouteService();

            // 填充城市下拉框
            List<String> cityNames = new ArrayList<>(routeService.getCityMap().keySet());
            Collections.sort(cityNames);
            startCityComboBox.setItems(FXCollections.observableArrayList(cityNames));
            endCityComboBox.setItems(FXCollections.observableArrayList(cityNames));

            // 填充景点下拉框
            List<String> attractionNames = new ArrayList<>(routeService.getAttractionMap().keySet());
            Collections.sort(attractionNames);
            attractionComboBox.setItems(FXCollections.observableArrayList(attractionNames));

            // 设置选定景点列表视图
            selectedAttractionsListView.setItems(selectedAttractions);

            // 设置按钮事件处理
            setupEventHandlers();

        } catch (IOException e) {
            showAlert("数据加载错误", "无法加载路线数据: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupEventHandlers() {
        // 添加景点按钮
        addAttractionButton.setOnAction(event -> {
            String selectedAttraction = attractionComboBox.getValue();
            if (selectedAttraction != null && !selectedAttractions.contains(selectedAttraction)) {
                selectedAttractions.add(selectedAttraction);
            }
        });

        // 删除景点按钮
        removeAttractionButton.setOnAction(event -> {
            String selected = selectedAttractionsListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectedAttractions.remove(selected);
            }
        });

        // 计算路线按钮
        calculateRouteButton.setOnAction(event -> calculateRoute());
    }

    private void calculateRoute() {
        String startCity = startCityComboBox.getValue();
        String endCity = endCityComboBox.getValue();

        if (startCity == null || endCity == null) {
            showAlert("输入错误", "请选择起始城市和目的地城市");
            return;
        }

        try {
            // 获取路线
            List<String> route = routeService.route(startCity, endCity, selectedAttractions);
            
            // 更新路线详情列表
            routeDetailsListView.setItems(FXCollections.observableArrayList(route));
            
            // 绘制地图
            drawRouteOnMap(startCity, endCity, selectedAttractions);
            
        } catch (Exception e) {
            showAlert("路线计算错误", "计算路线时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void drawRouteOnMap(String startCity, String endCity, List<String> attractions) {
        GraphicsContext gc = mapCanvas.getGraphicsContext2D();
        double width = mapCanvas.getWidth();
        double height = mapCanvas.getHeight();
        
        // 清除画布
        gc.clearRect(0, 0, width, height);
        
        // 绘制简单的美国地图轮廓
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(2);
        gc.strokeRect(10, 10, width - 20, height - 20);
        
        // 获取城市和景点的坐标
        Map<String, double[]> points = new HashMap<>();
        
        // 添加起点和终点
        if (routeService.getCityMap().containsKey(startCity)) {
            double[] coords = normalizeCoordinates(routeService.getCityMap().get(startCity), width, height);
            points.put(startCity, coords);
        }
        
        if (routeService.getCityMap().containsKey(endCity)) {
            double[] coords = normalizeCoordinates(routeService.getCityMap().get(endCity), width, height);
            points.put(endCity, coords);
        }
        
        // 添加景点坐标
        for (String attraction : attractions) {
            Attraction attr = routeService.getAttractionMap().get(attraction);
            if (attr != null) {
                String city = attr.getLocation();
                if (routeService.getCityMap().containsKey(city)) {
                    double[] coords = normalizeCoordinates(routeService.getCityMap().get(city), width, height);
                    points.put(attraction, coords);
                }
            }
        }
        
        // 绘制点和线
        List<String> pointsOrder = routeService.getVisitOrder(startCity, endCity, attractions);
        
        if (pointsOrder.size() > 1) {
            // 绘制线
            gc.setStroke(Color.BLUE);
            gc.setLineWidth(2);
            
            for (int i = 0; i < pointsOrder.size() - 1; i++) {
                String from = pointsOrder.get(i);
                String to = pointsOrder.get(i + 1);
                
                // 如果是景点，获取其所在城市
                if (routeService.getAttractionMap().containsKey(from)) {
                    from = routeService.getAttractionMap().get(from).getLocation();
                }
                if (routeService.getAttractionMap().containsKey(to)) {
                    to = routeService.getAttractionMap().get(to).getLocation();
                }
                
                if (points.containsKey(from) && points.containsKey(to)) {
                    double[] fromCoords = points.get(from);
                    double[] toCoords = points.get(to);
                    
                    gc.strokeLine(fromCoords[0], fromCoords[1], toCoords[0], toCoords[1]);
                }
            }
            
            // 绘制点
            for (Map.Entry<String, double[]> entry : points.entrySet()) {
                String name = entry.getKey();
                double[] coords = entry.getValue();
                
                if (name.equals(startCity)) {
                    gc.setFill(Color.GREEN);
                    gc.fillOval(coords[0] - 7, coords[1] - 7, 14, 14);
                } else if (name.equals(endCity)) {
                    gc.setFill(Color.RED);
                    gc.fillOval(coords[0] - 7, coords[1] - 7, 14, 14);
                } else if (routeService.getAttractionMap().containsKey(name)) {
                    gc.setFill(Color.ORANGE);
                    gc.fillOval(coords[0] - 5, coords[1] - 5, 10, 10);
                }
                
                // 绘制标签
                gc.setFill(Color.BLACK);
                gc.fillText(name, coords[0] + 10, coords[1]);
            }
        }
    }

    private double[] normalizeCoordinates(double[] coordinates, double width, double height) {
        // 假设输入的坐标是经纬度，需要将其转换为画布坐标
        // 美国大陆的经纬度范围大约是：经度 -125 到 -65，纬度 25 到 50
        double minLon = -125;
        double maxLon = -65;
        double minLat = 25;
        double maxLat = 50;
        
        double lon = coordinates[0];
        double lat = coordinates[1];
        
        // 正规化到0-1的范围
        double normalizedLon = (lon - minLon) / (maxLon - minLon);
        double normalizedLat = 1 - (lat - minLat) / (maxLat - minLat); // 翻转纬度，因为画布y轴向下
        
        // 转换为画布坐标
        double x = 20 + normalizedLon * (width - 40);
        double y = 20 + normalizedLat * (height - 40);
        
        return new double[] {x, y};
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 