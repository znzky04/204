package com.roadtrip.gui;

import com.roadtrip.model.Attraction;
import com.roadtrip.model.City;
import com.roadtrip.model.Graph;
import com.roadtrip.service.RouteService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.layout.Region;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * 路线规划界面的增强版控制器类
 */
public class RoadTripController {

    // FXML组件注入
    @FXML private ComboBox<String> startingCityComboBox;
    @FXML private ComboBox<String> destinationCityComboBox;
    @FXML private ComboBox<String> attractionComboBox;
    @FXML private ListView<String> selectedAttractionsListView;
    @FXML private ListView<String> routeDetailsListView;
    @FXML private TextField attractionSearchField;
    @FXML private Button addAttractionButton;
    @FXML private Button removeAttractionButton;
    @FXML private Button planRouteButton;
    @FXML private Button swapCitiesButton;
    @FXML private Button exportRouteButton;
    @FXML private Button resetViewButton;
    @FXML private Canvas mapCanvas;
    @FXML private Canvas overlayCanvas;
    @FXML private BorderPane rootPane;
    @FXML private ToggleButton networkViewToggleButton;
    @FXML private CheckBox showDistanceCheckBox;
    @FXML private CheckBox showCityNamesCheckBox;
    @FXML private ColorPicker routeColorPicker;
    @FXML private ColorPicker backgroundColorPicker;
    @FXML private Label statusLabel;
    @FXML private Label totalDistanceLabel;
    @FXML private Label mapStatusLabel;
    @FXML private ProgressBar progressBar;
    @FXML private Button zoomInButton;
    @FXML private Button zoomOutButton;
    @FXML private Button panLeftButton;
    @FXML private Button panRightButton;
    @FXML private Button panUpButton;
    @FXML private Button panDownButton;
    @FXML private ToggleButton editModeToggle;
    
    // 数据模型
    private List<String> allCities = new ArrayList<>();
    private List<String> allAttractions = new ArrayList<>();
    private ObservableList<String> selectedAttractions = FXCollections.observableArrayList();
    private RouteService routeService;
    private List<String> currentRouteResults;
    
    // 地图状态变量
    private double mapScale = 1.0;
    private double mapOffsetX = 0;
    private double mapOffsetY = 0;
    private boolean showNetwork = false;
    private boolean editMode = false;
    private Color routeColor = Color.BLUE;
    private Color backgroundColor = Color.LIGHTBLUE;
    private Map<String, double[]> cityCoordinates = new HashMap<>();
    private List<String> optimalRoute = new ArrayList<>();
    private double totalDistance = 0;
    
    // 可视化模式设置
    private boolean completeMapMode = false;  // 默认为简洁模式
    private Color networkColor = Color.DARKGRAY;  // 改为深灰色以提高可见性
    private Color shortestPathColor = Color.BLUE;  // 最短路径高亮色
    private Color cityColor = Color.DARKSLATEBLUE;
    private Color startCityColor = Color.GREEN;
    private Color endCityColor = Color.RED;
    private Color routeCityColor = Color.ORANGE;
    private Color attractionColor = Color.PURPLE;
    
    // 初始化缩放和平移步长
    private final double ZOOM_FACTOR = 1.2;
    private final double PAN_STEP = 50.0;
    
    /**
     * 初始化控制器
     */
    @FXML
    public void initialize() {
        try {
            // 初始化路线服务
            routeService = new RouteService();
            
            // 加载所有城市和景点数据
            loadCitiesAndAttractions();
            
            // 初始化UI组件
            setupUIComponents();
            
            // 绑定监听器
            bindEventListeners();
            
            // 初始化地图
            initializeMap();
            
            // 设置初始状态
            statusLabel.setText("就绪");
            
            // 设置颜色选择器的初始值
            routeColorPicker.setValue(routeColor);
            backgroundColorPicker.setValue(backgroundColor);
            
        } catch (IOException e) {
            showErrorAlert("初始化错误", "加载数据时出错: " + e.getMessage());
        }
    }
    
    /**
     * 加载所有城市和景点数据
     */
    private void loadCitiesAndAttractions() {
        // 获取所有城市
        Map<String, City> cityMap = routeService.getCityMap();
        allCities.addAll(cityMap.keySet());
        allCities.sort(String::compareTo);
        
        // 获取所有景点
        Map<String, Attraction> attractionMap = routeService.getAttractionMap();
        allAttractions.addAll(attractionMap.keySet());
        allAttractions.sort(String::compareTo);
        
        // 预计算所有城市的相对坐标
        calculateCityCoordinates();
    }
    
    /**
     * 计算所有城市的相对坐标（用于地图显示）
     */
    private void calculateCityCoordinates() {
        // 清空现有坐标
        cityCoordinates.clear();
        
        // 使用硬编码的美国主要城市经纬度（实际值）
        // 经度: 西部为负，东部较大；纬度: 北部为正，南部较小
        Map<String, double[]> cityLatLong = new HashMap<>();
        // 西海岸城市
        cityLatLong.put("Los Angeles CA", new double[]{34.05, -118.24});
        cityLatLong.put("San Diego CA", new double[]{32.72, -117.16});
        cityLatLong.put("San Jose CA", new double[]{37.34, -121.89});
        // 中部城市
        cityLatLong.put("Phoenix AZ", new double[]{33.45, -112.07});
        cityLatLong.put("Dallas TX", new double[]{32.78, -96.80});
        cityLatLong.put("Fort Worth TX", new double[]{32.75, -97.33});
        cityLatLong.put("San Antonio TX", new double[]{29.42, -98.49});
        cityLatLong.put("Austin TX", new double[]{30.27, -97.74});
        cityLatLong.put("Houston TX", new double[]{29.76, -95.36});
        cityLatLong.put("Chicago IL", new double[]{41.88, -87.63});
        cityLatLong.put("Columbus OH", new double[]{39.96, -82.99});
        // 东部城市
        cityLatLong.put("New York NY", new double[]{40.71, -74.01});
        cityLatLong.put("Philadelphia PA", new double[]{39.95, -75.17});
        cityLatLong.put("Jacksonville FL", new double[]{30.33, -81.66});
        cityLatLong.put("Charlotte NC", new double[]{35.23, -80.84});
        
        // 找出经纬度范围
        double minLat = 24.0;   // 南佛罗里达
        double maxLat = 49.0;   // 北边界
        double minLon = -125.0; // 西海岸
        double maxLon = -66.0;  // 东海岸
        
        System.out.println("美国地图范围: 经度[" + minLon + ", " + maxLon + "], 纬度[" + minLat + ", " + maxLat + "]");
        
        // 为每个城市计算0-1范围内的相对坐标
        for (Map.Entry<String, City> entry : routeService.getCityMap().entrySet()) {
            String cityName = entry.getKey();
            
            // 获取城市的经纬度
            double[] latLong = cityLatLong.get(cityName);
            if (latLong != null) {
                // 转换为相对坐标 (经度从负到正，所以需要将负值转换为正值)
                double normalizedX = (latLong[1] - minLon) / (maxLon - minLon);
                double normalizedY = 1.0 - (latLong[0] - minLat) / (maxLat - minLat);  // 反转纬度
                
                cityCoordinates.put(cityName, new double[]{normalizedX, normalizedY});
                System.out.println("城市: " + cityName + ", 坐标: [" + 
                    normalizedX + ", " + normalizedY + "], 经纬度: [" + 
                    latLong[1] + ", " + latLong[0] + "]");
            } else {
                // 找不到经纬度，使用随机坐标（但实际项目应该找出所有城市的准确经纬度）
            City city = entry.getValue();
                double randX = 0.1 + Math.random() * 0.8; // 10%-90%范围
                double randY = 0.1 + Math.random() * 0.8;
                cityCoordinates.put(cityName, new double[]{randX, randY});
                System.out.println("城市: " + cityName + ", 随机坐标: [" + randX + ", " + randY + "]");
            }
        }
    }
    
    /**
     * 设置UI组件初始状态
     */
    private void setupUIComponents() {
        // 设置城市下拉框
        startingCityComboBox.setItems(FXCollections.observableArrayList(allCities));
        destinationCityComboBox.setItems(FXCollections.observableArrayList(allCities));
        
        // 设置景点下拉框
        attractionComboBox.setItems(FXCollections.observableArrayList(allAttractions));
        
        // 设置已选择的景点列表
        selectedAttractionsListView.setItems(selectedAttractions);
        
        // 设置路线详情列表样式
        routeDetailsListView.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    
                    if (item.startsWith("错误:")) {
                        setTextFill(Color.RED);
                        setStyle("-fx-font-weight: bold;");
                    } else if (item.startsWith("Starting from:")) {
                        setTextFill(Color.GREEN);
                        setStyle("-fx-font-weight: bold;");
                    } else if (item.startsWith("Total distance:")) {
                        setTextFill(Color.BLUE);
                        setStyle("-fx-font-weight: bold;");
                    } else if (item.contains("(Visiting:")) {
                        setTextFill(Color.PURPLE);
                    } else {
                        setTextFill(Color.BLACK);
                        setStyle("");
                    }
                }
            }
        });
        
        // 处理景点搜索
        attractionSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterAttractions(newValue);
        });
        
        // 使Canvas的尺寸与父容器保持一致 - 确保Canvas填满整个区域
        StackPane canvasParent = (StackPane) mapCanvas.getParent();
        
        // 移除任何可能限制Canvas尺寸的属性
        mapCanvas.setWidth(Region.USE_COMPUTED_SIZE);
        mapCanvas.setHeight(Region.USE_COMPUTED_SIZE);
        overlayCanvas.setWidth(Region.USE_COMPUTED_SIZE);
        overlayCanvas.setHeight(Region.USE_COMPUTED_SIZE);
        
        // 强制绑定Canvas尺寸到父容器尺寸
        mapCanvas.widthProperty().bind(canvasParent.widthProperty());
        mapCanvas.heightProperty().bind(canvasParent.heightProperty());
        overlayCanvas.widthProperty().bind(canvasParent.widthProperty());
        overlayCanvas.heightProperty().bind(canvasParent.heightProperty());
        
        // 确保Canvas位于中心
        mapCanvas.setTranslateX(0);
        mapCanvas.setTranslateY(0);
        overlayCanvas.setTranslateX(0);
        overlayCanvas.setTranslateY(0);
        
        // 监听Canvas大小变化，重绘地图
        mapCanvas.widthProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("Canvas width changed: " + oldVal + " -> " + newVal);
            redrawMap();
        });
        mapCanvas.heightProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("Canvas height changed: " + oldVal + " -> " + newVal);
            redrawMap();
        });
    }
    
    /**
     * 根据搜索文本过滤景点列表
     */
    private void filterAttractions(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            attractionComboBox.setItems(FXCollections.observableArrayList(allAttractions));
        } else {
            String lowerCaseFilter = searchText.toLowerCase();
            List<String> filteredList = allAttractions.stream()
                    .filter(attraction -> attraction.toLowerCase().contains(lowerCaseFilter))
                    .toList();
            attractionComboBox.setItems(FXCollections.observableArrayList(filteredList));
        }
    }
    
    /**
     * 绑定UI事件监听器
     */
    private void bindEventListeners() {
        // 添加景点按钮
        addAttractionButton.setOnAction(event -> addAttraction());
        
        // 移除景点按钮
        removeAttractionButton.setOnAction(event -> {
            String selected = selectedAttractionsListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectedAttractions.remove(selected);
            }
        });
        
        // 计算路线按钮
        planRouteButton.setOnAction(event -> calculateRoute());
        
        // 交换起点和终点按钮
        swapCitiesButton.setOnAction(event -> {
            String temp = startingCityComboBox.getValue();
            startingCityComboBox.setValue(destinationCityComboBox.getValue());
            destinationCityComboBox.setValue(temp);
        });
        
        // 导出路线按钮
        exportRouteButton.setOnAction(event -> exportRoute());
        
        // 颜色选择器
        routeColorPicker.setOnAction(event -> {
            routeColor = routeColorPicker.getValue();
            redrawMap();
        });
        
        backgroundColorPicker.setOnAction(event -> {
            backgroundColor = backgroundColorPicker.getValue();
            redrawMap();
        });
        
        // 显示选项复选框
        showDistanceCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> redrawMap());
        showCityNamesCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> redrawMap());
        
        // 添加双击地图选择城市功能
        overlayCanvas.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                handleMapClick(event);
            }
        });
    }
    
    /**
     * 初始化地图
     */
    private void initializeMap() {
        // 在JavaFX应用线程中运行，确保UI元素已经正确初始化
        Platform.runLater(() -> {
            double width = mapCanvas.getWidth();
            double height = mapCanvas.getHeight();
            
            System.out.println("初始化地图，Canvas尺寸: " + width + "x" + height);
            
            GraphicsContext gc = mapCanvas.getGraphicsContext2D();
            gc.setFill(backgroundColor);
            gc.fillRect(0, 0, width, height);
            
            gc.setFill(Color.BLACK);
            gc.setFont(new Font(gc.getFont().getName(), 16));
            gc.fillText("请选择起点、终点和景点，然后点击规划路线按钮", 20, height / 2);
            
            // 允许双击地图选择城市
            overlayCanvas.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    handleMapClick(event);
                }
            });
        });
    }
    
    /**
     * 处理地图点击事件
     */
    private void handleMapClick(MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();
        
        // 查找离点击位置最近的城市
        String closestCity = findClosestCity(mouseX, mouseY);
        if (closestCity != null) {
            // 如果起点城市未设置，设置起点
            if (startingCityComboBox.getValue() == null) {
                startingCityComboBox.setValue(closestCity);
                statusLabel.setText("已设置起点：" + closestCity);
            } 
            // 如果起点已设置但终点未设置，设置终点
            else if (destinationCityComboBox.getValue() == null) {
                destinationCityComboBox.setValue(closestCity);
                statusLabel.setText("已设置终点：" + closestCity);
            } 
            // 如果起点和终点都已设置，则重新设置起点
            else {
                startingCityComboBox.setValue(closestCity);
                statusLabel.setText("已重新设置起点：" + closestCity);
            }
        }
    }
    
    /**
     * 找到离指定坐标最近的城市
     */
    private String findClosestCity(double x, double y) {
        double canvasWidth = mapCanvas.getWidth();
        double canvasHeight = mapCanvas.getHeight();
        
        String closestCity = null;
        double closestDistance = Double.MAX_VALUE;
        
        for (Map.Entry<String, double[]> entry : cityCoordinates.entrySet()) {
            double[] coords = entry.getValue();
            double cityX = coords[0] * canvasWidth;
            double cityY = coords[1] * canvasHeight;
            
            double distance = Math.sqrt(Math.pow(x - cityX, 2) + Math.pow(y - cityY, 2));
            
            if (distance < closestDistance && distance < 30) { // 30像素的阈值
                closestDistance = distance;
                closestCity = entry.getKey();
            }
        }
        
        return closestCity;
    }
    
    /**
     * 添加景点到已选列表
     */
    private void addAttraction() {
        String selectedAttraction = attractionComboBox.getValue();
        if (selectedAttraction != null && !selectedAttractions.contains(selectedAttraction)) {
            selectedAttractions.add(selectedAttraction);
            statusLabel.setText("已添加景点: " + selectedAttraction);
        }
    }
    
    /**
     * 计算并显示路线
     */
    private void calculateRoute() {
        String startCity = startingCityComboBox.getValue();
        String endCity = destinationCityComboBox.getValue();
        
        // 检查起点和终点是否已选择
        if (startCity == null || endCity == null) {
            showErrorAlert("输入错误", "请选择起点和终点城市");
            return;
        }
        
        // 显示进度条
        progressBar.setVisible(true);
        progressBar.setProgress(-1);  // 不确定进度
        statusLabel.setText("正在计算路线...");
        
        // 使用后台线程计算路线，避免UI冻结
        new Thread(() -> {
            try {
                // 获取路线结果
                List<String> results = routeService.route(startCity, endCity, selectedAttractions);
                currentRouteResults = results;
                
                // 解析总距离
                for (String line : results) {
                    if (line.startsWith("Total distance:")) {
                        String distanceStr = line.substring("Total distance:".length()).trim();
                        distanceStr = distanceStr.replace(" miles", "");
                        try {
                            totalDistance = Double.parseDouble(distanceStr);
                        } catch (NumberFormatException e) {
                            totalDistance = 0;
                        }
                        break;
                    }
                }
                
                // 提取路线城市序列
                optimalRoute.clear();
                for (String line : results) {
                    if (line.startsWith("Starting from: ")) {
                        optimalRoute.add(line.substring("Starting from: ".length()));
                    } else if (!line.startsWith("Total distance:") && !line.isEmpty()) {
                        String cityName = line;
                        if (line.contains(" (Visiting:")) {
                            cityName = line.substring(0, line.indexOf(" (Visiting:"));
                        }
                        optimalRoute.add(cityName);
                    }
                }
                
                // 在UI线程更新界面
                Platform.runLater(() -> {
                    // 更新路线详情列表
                    routeDetailsListView.setItems(FXCollections.observableArrayList(results));
                    
                    // 更新总距离标签
                    totalDistanceLabel.setText("总距离: " + totalDistance + " miles");
                    
                    // 重绘地图
                    redrawMap();
                    
                    // 隐藏进度条，更新状态
                    progressBar.setVisible(false);
                    statusLabel.setText("路线计算完成");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showErrorAlert("路线计算错误", "计算路线时发生错误: " + e.getMessage());
                    progressBar.setVisible(false);
                    statusLabel.setText("路线计算失败");
                });
            }
        }).start();
    }
    
    /**
     * 重新绘制地图
     */
    private void redrawMap() {
        double width = mapCanvas.getWidth();
        double height = mapCanvas.getHeight();
        
        // 检查Canvas是否有效尺寸
        if (width <= 0 || height <= 0) {
            System.out.println("警告: Canvas尺寸无效: " + width + "x" + height);
            return;
        }
        
        System.out.println("重绘地图，Canvas尺寸: " + width + "x" + height);
        
        GraphicsContext gc = mapCanvas.getGraphicsContext2D();
        GraphicsContext overlayGc = overlayCanvas.getGraphicsContext2D();
        
        // 清除两个画布
        gc.clearRect(0, 0, width, height);
        overlayGc.clearRect(0, 0, width, height);
        
        // 绘制背景 - 确保填满整个Canvas
        gc.setFill(backgroundColor);
        gc.fillRect(0, 0, width, height);
        
        // 获取服务对象以访问图形数据
        Graph graph = routeService.getGraph();
        Map<String, City> cityMap = routeService.getCityMap();
        Map<String, Attraction> attractionMap = routeService.getAttractionMap();
        
        // 先检查cityCoordinates中是否有数据
        if (cityCoordinates.isEmpty()) {
            calculateCityCoordinates();
        }
        
        // 首先绘制所有路线连接
        drawAllConnections(gc, width, height, graph);
        
        // 绘制所有城市
        drawAllCities(gc, width, height, cityMap);
        
        // 绘制所有景点
        drawAllAttractions(gc, width, height, attractionMap);
        
        // 如果计算了路线，高亮显示最短路径
        if (!optimalRoute.isEmpty()) {
            // 绘制最优路线
            drawOptimalRoute(overlayGc, width, height);
            
            // 绘制路线上的城市节点
            drawRouteCities(overlayGc, width, height, cityMap);
        
        // 绘制距离标签
        if (showDistanceCheckBox.isSelected()) {
                drawDistanceLabels(overlayGc, width, height);
            }
        } else {
            // 如果没有计算路线，显示提示信息
            gc.setFill(Color.BLACK);
            gc.setFont(new Font(gc.getFont().getName(), 16));
            gc.fillText("请选择起点、终点和景点，然后点击规划路线按钮", 20, height / 2);
        }
    }
    
    /**
     * 绘制所有连接线
     */
    private void drawAllConnections(GraphicsContext gc, double width, double height, Graph graph) {
        // 绘制所有路线连接（细线）
        gc.setStroke(networkColor);
        gc.setLineWidth(1.5);  // 加粗线条宽度
        
        for (Graph.Edge edge : graph.getAllEdges()) {
            String city1 = edge.getCity1();
            String city2 = edge.getCity2();
            
            double[] coords1 = cityCoordinates.get(city1);
            double[] coords2 = cityCoordinates.get(city2);
                
                if (coords1 != null && coords2 != null) {
                // 映射到画布坐标
                double x1 = coords1[0] * width;
                double y1 = coords1[1] * height;
                double x2 = coords2[0] * width;
                double y2 = coords2[1] * height;
                    
                    gc.strokeLine(x1, y1, x2, y2);
                
                // 调试输出连接线坐标
                System.out.println("绘制连接: " + city1 + " -> " + city2 + ", 线: [" + 
                    x1 + "," + y1 + "] -> [" + x2 + "," + y2 + "]");
            }
        }
    }
    
    /**
     * 绘制所有城市
     */
    private void drawAllCities(GraphicsContext gc, double width, double height, Map<String, City> cityMap) {
        double cityRadius = 6;  // 增大城市点的大小
        Map<String, double[]> usedPositions = new HashMap<>(); // 追踪已使用的标签位置
        
        // 首先绘制普通城市（非路线城市）
        for (Map.Entry<String, City> entry : cityMap.entrySet()) {
            String cityName = entry.getKey();
            
            // 跳过路线上的城市，它们在drawRouteCities方法中单独处理
            if (optimalRoute.contains(cityName)) continue;
            
            double[] coords = cityCoordinates.get(cityName);
            
            if (coords != null) {
                double x = coords[0] * width;
                double y = coords[1] * height;
                
                // 普通城市
                gc.setFill(cityColor);
                gc.fillOval(x - cityRadius, y - cityRadius, cityRadius * 2, cityRadius * 2);
                
                // 如果设置显示城市名称，绘制名称
                if (showCityNamesCheckBox.isSelected()) {
                    // 检查标签位置是否重叠，如果重叠则调整位置
                    double labelX = x + cityRadius * 1.5;
                    double labelY = y;
                    
                    // 尝试移动标签到下方，避免与周围标签重叠
                    double[] positions = findAvailableLabelPosition(usedPositions, labelX, labelY, 70);
                    labelX = positions[0];
                    labelY = positions[1];
                    
                    // 记录标签位置
                    usedPositions.put(cityName, new double[]{labelX, labelY});
                    
                    // 绘制标签
                    drawCityLabel(gc, cityName, labelX, labelY, cityRadius, Color.BLACK);
                }
            }
        }
    }
    
    /**
     * 寻找可用的标签位置，避免重叠
     */
    private double[] findAvailableLabelPosition(Map<String, double[]> usedPositions, double x, double y, double minDistance) {
        // 如果没有其他标签或位置不冲突，直接返回原位置
        if (usedPositions.isEmpty()) {
            return new double[]{x, y};
        }
        
        // 检查原位置是否与其他标签冲突
        boolean isConflict = false;
        for (double[] pos : usedPositions.values()) {
            double dx = pos[0] - x;
            double dy = pos[1] - y;
            double distance = Math.sqrt(dx*dx + dy*dy);
            if (distance < minDistance) {
                isConflict = true;
                break;
            }
        }
        
        // 如果没有冲突，直接返回原位置
        if (!isConflict) {
            return new double[]{x, y};
        }
        
        // 尝试不同方向的偏移 - 增加更多可能的偏移位置和距离
        double[][] offsets = {
            {0, 30},    // 正下方
            {30, 0},    // 正右侧
            {0, -30},   // 正上方
            {-30, 0},   // 正左侧
            {30, 30},   // 右下
            {-30, 30},  // 左下
            {30, -30},  // 右上
            {-30, -30}, // 左上
            {0, 50},    // 更远的下方
            {50, 0},    // 更远的右侧
            {0, -50},   // 更远的上方
            {-50, 0},   // 更远的左侧
            {50, 50},   // 更远的右下
            {-50, 50},  // 更远的左下
            {50, -50},  // 更远的右上
            {-50, -50}  // 更远的左上
        };
        
        // 尝试每个偏移直到找到无冲突的位置
        for (double[] offset : offsets) {
            double newX = x + offset[0];
            double newY = y + offset[1];
            
            boolean foundConflict = false;
            for (double[] pos : usedPositions.values()) {
                double dx = pos[0] - newX;
                double dy = pos[1] - newY;
                double distance = Math.sqrt(dx*dx + dy*dy);
                if (distance < minDistance) {
                    foundConflict = true;
                    break;
                }
            }
            
            if (!foundConflict) {
                return new double[]{newX, newY};
            }
        }
        
        // 如果所有尝试都失败，尝试更远的随机位置
        for (int attempt = 0; attempt < 5; attempt++) {
            // 随机角度
            double angle = Math.random() * 2 * Math.PI;
            // 60-80像素的随机距离
            double distance = 60 + Math.random() * 20;
            
            double newX = x + Math.cos(angle) * distance;
            double newY = y + Math.sin(angle) * distance;
            
            boolean foundConflict = false;
            for (double[] pos : usedPositions.values()) {
                double dx = pos[0] - newX;
                double dy = pos[1] - newY;
                double dist = Math.sqrt(dx*dx + dy*dy);
                if (dist < minDistance) {
                    foundConflict = true;
                    break;
                }
            }
            
            if (!foundConflict) {
                return new double[]{newX, newY};
            }
        }
        
        // 如果所有尝试都失败，返回一个更远的位置以减少重叠
        return new double[]{x + 20, y + 40};
    }
    
    /**
     * 绘制城市标签
     */
    private void drawCityLabel(GraphicsContext gc, String cityName, double x, double y, double baseRadius, Color textColor) {
        if (!showCityNamesCheckBox.isSelected()) return;
        
        // 避免为路线上的城市再次绘制标签（因为这些已经在drawRouteCities中处理过）
        if (optimalRoute.contains(cityName)) {
            return;
        }
        
        // 绘制文本背景
        String displayName = cityName;
        double textWidth = displayName.length() * 7;
        double textHeight = 18;
        
        gc.setFill(Color.WHITE);
        gc.fillRoundRect(x, y - textHeight/2, textWidth, textHeight, 5, 5);
        
        // 绘制文本
        gc.setFill(textColor);
        gc.setFont(new Font(gc.getFont().getName(), 11));
        gc.fillText(cityName, x + 2, y + 4);
    }
    
    /**
     * 绘制所有景点
     */
    private void drawAllAttractions(GraphicsContext gc, double width, double height, Map<String, Attraction> attractionMap) {
        double attractionSize = 10;  // 基础景点大小
        Map<String, Boolean> drawnLabels = new HashMap<>(); // 用于跟踪已绘制的标签
        Map<String, double[]> labelPositions = new HashMap<>(); // 用于跟踪标签位置
        
        // 先确保所有选中的景点总是显示
        for (String attractionName : selectedAttractions) {
            Attraction attraction = attractionMap.get(attractionName);
            if (attraction != null) {
                String cityName = attraction.getLocation().toString();
                double[] coords = cityCoordinates.get(cityName);
                
                if (coords != null) {
                    double x = coords[0] * width;
                    double y = coords[1] * height - 30;  // 显示在城市上方，高一些
                    
                    // 检查选中景点的标签位置
                    double[] labelPos = findAvailableLabelPosition(labelPositions, x, y, 80);
                    
                    // 记录标签位置
                    labelPositions.put(attractionName, labelPos);
                    drawnLabels.put(attractionName, true);
                }
            }
        }
        
        // 首先绘制非选中的景点（这样选中的景点会在上面）
        for (Map.Entry<String, Attraction> entry : attractionMap.entrySet()) {
            String attractionName = entry.getKey();
            Attraction attraction = entry.getValue();
            
            // 跳过已选中的景点，我们稍后再绘制它们
            if (selectedAttractions.contains(attractionName)) continue;
            
            if (attraction != null) {
                String cityName = attraction.getLocation().toString();
                double[] coords = cityCoordinates.get(cityName);
                
                if (coords != null) {
                    double x = coords[0] * width;
                    double y = coords[1] * height - 20;  // 显示在城市上方
                    
                    // 绘制非选中景点，但不显示名称
                    drawNonSelectedAttraction(gc, attractionName, x, y);
                }
            }
        }
        
        // 然后绘制选中的景点，确保它们在最上面
        for (String attractionName : selectedAttractions) {
            Attraction attraction = attractionMap.get(attractionName);
            if (attraction != null) {
                String cityName = attraction.getLocation().toString();
                double[] coords = cityCoordinates.get(cityName);
                
                if (coords != null) {
                    double x = coords[0] * width;
                    double y = coords[1] * height - 30;  // 显示在城市上方，高一些
                    
                    // 使用预先计算的标签位置
                    double[] labelPos = labelPositions.get(attractionName);
                    
                    // 绘制选中景点，强制显示标签
                    drawSelectedAttraction(gc, attractionName, x, y, labelPos);
                }
            }
        }
    }
    
    /**
     * 绘制未选中的景点
     */
    private void drawNonSelectedAttraction(GraphicsContext gc, String attractionName, double x, double y) {
        // 普通景点使用粉色菱形，较小尺寸
        gc.setFill(Color.LIGHTPINK);
        double normalSize = 8;
        
        // 绘制景点图标（菱形）
        double[] xPoints = {x, x + normalSize/2, x, x - normalSize/2};
        double[] yPoints = {y - normalSize/2, y, y + normalSize/2, y};
        gc.fillPolygon(xPoints, yPoints, 4);
        
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.0);
        gc.strokePolygon(xPoints, yPoints, 4);
        
        // 非选中景点不显示名称
    }
    
    /**
     * 绘制已选中的景点
     */
    private void drawSelectedAttraction(GraphicsContext gc, String attractionName, double x, double y, double[] labelPos) {
        // 选中的景点使用黄色菱形，更大尺寸
        gc.setFill(Color.YELLOW);
        double selectedSize = 14;
        
        // 绘制景点图标（菱形）
        double[] xPoints = {x, x + selectedSize/2, x, x - selectedSize/2};
        double[] yPoints = {y - selectedSize/2, y, y + selectedSize/2, y};
        gc.fillPolygon(xPoints, yPoints, 4);
        
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.5);
        gc.strokePolygon(xPoints, yPoints, 4);
        
        // 绘制景点名称
        String displayName = attractionName;
        double textWidth = displayName.length() * 7;
        
        // 使用调整后的标签位置
        double labelX = labelPos[0];
        double labelY = labelPos[1];
        
        // 使用半透明深色背景，使文字在浅色背景上更易读
        gc.setFill(Color.rgb(50, 50, 50, 0.7));
        gc.fillRoundRect(labelX - textWidth/2 - 3, 
                        labelY, 
                        textWidth + 6, 20, 5, 5);
        
        // 使用黄色文本
        gc.setFill(Color.YELLOW);
        gc.setFont(new Font(gc.getFont().getName(), 12));
        gc.fillText(attractionName, labelX - textWidth/2, labelY + 14);
    }
    
    /**
     * 绘制最优路线
     */
    private void drawOptimalRoute(GraphicsContext gc, double width, double height) {
        if (optimalRoute.size() < 2) return;
        
        // 绘制高亮路径
        gc.setStroke(routeColor);
        gc.setLineWidth(4.5);  // 使用更粗的线突出路径
        
        for (int i = 0; i < optimalRoute.size() - 1; i++) {
            String city1 = optimalRoute.get(i);
            String city2 = optimalRoute.get(i + 1);
            
            double[] coords1 = cityCoordinates.get(city1);
            double[] coords2 = cityCoordinates.get(city2);
            
            if (coords1 != null && coords2 != null) {
                double x1 = coords1[0] * width;
                double y1 = coords1[1] * height;
                double x2 = coords2[0] * width;
                double y2 = coords2[1] * height;
                
                // 绘制高亮路径
                gc.strokeLine(x1, y1, x2, y2);
                
                // 绘制路径上的箭头
                drawArrow(gc, x1, y1, x2, y2);
            }
        }
    }
    
    /**
     * 绘制路线上的城市节点（突出显示）
     */
    private void drawRouteCities(GraphicsContext gc, double width, double height, Map<String, City> cityMap) {
        double baseRadius = 8;
        Map<String, double[]> usedPositions = new HashMap<>(); // 追踪已使用的标签位置
        
        // 先为起点和终点预留位置
        for (int i = 0; i < optimalRoute.size(); i++) {
            String cityName = optimalRoute.get(i);
            double[] coords = cityCoordinates.get(cityName);
            
            if (coords != null) {
                double x = coords[0] * width;
                double y = coords[1] * height;
                
                boolean isStart = i == 0;
                boolean isEnd = i == optimalRoute.size() - 1;
                
                // 根据城市角色确定颜色和大小
                if (isStart || isEnd) {
                    // 计算标签位置并检查是否冲突
                    double labelX = x;
                    double labelY = y + baseRadius * 3;
                    // 增加最小距离要求，提高标签间距
                    double[] positions = findAvailableLabelPosition(usedPositions, labelX, labelY, 85);
                    
                    // 记录标签位置
                    usedPositions.put(cityName, positions);
                }
            }
        }
        
        // 然后为中间城市添加标签
        for (int i = 0; i < optimalRoute.size(); i++) {
            String cityName = optimalRoute.get(i);
            double[] coords = cityCoordinates.get(cityName);
            
            if (coords != null) {
                double x = coords[0] * width;
                double y = coords[1] * height;
                
                boolean isStart = i == 0;
                boolean isEnd = i == optimalRoute.size() - 1;
                
                // 根据城市角色确定颜色和大小
                if (isStart) {
                    // 起点 - 绿色圆形
                    gc.setFill(startCityColor);
                    gc.fillOval(x - baseRadius * 1.8, y - baseRadius * 1.8, 
                               baseRadius * 3.6, baseRadius * 3.6);
                    
                    // 添加"起点"标记
                    gc.setFill(Color.WHITE);
                    gc.setFont(new Font(gc.getFont().getName(), 12));
                    gc.fillText("起点", x - 12, y - baseRadius * 2);
                    
                    // 使用预先计算好的位置
                    double[] positions = usedPositions.get(cityName);
                    
                    // 绘制城市名称标签，放置在城市下方以避免与"起点"标记重叠
                    drawStartEndCityLabel(gc, cityName, positions[0], positions[1], Color.BLACK);
                } 
                else if (isEnd) {
                    // 终点 - 红色圆形
                    gc.setFill(endCityColor);
                    gc.fillOval(x - baseRadius * 1.8, y - baseRadius * 1.8, 
                               baseRadius * 3.6, baseRadius * 3.6);
                    
                    // 添加"终点"标记
                    gc.setFill(Color.WHITE);
                    gc.setFont(new Font(gc.getFont().getName(), 12));
                    gc.fillText("终点", x - 12, y - baseRadius * 2);
                    
                    // 使用预先计算好的位置
                    double[] positions = usedPositions.get(cityName);
                    
                    // 绘制城市名称标签，放置在城市下方以避免与"终点"标记重叠
                    drawStartEndCityLabel(gc, cityName, positions[0], positions[1], Color.BLACK);
                } 
                else {
                    // 中间城市 - 橙色圆形
                    gc.setFill(routeCityColor);
                    gc.fillOval(x - baseRadius, y - baseRadius, 
                               baseRadius * 2, baseRadius * 2);
                    
                    // 计算标签位置并检查是否冲突
                    double labelX = x;
                    double labelY = y + baseRadius * 1.5;
                    // 增加最小距离要求，提高标签间距
                    double[] positions = findAvailableLabelPosition(usedPositions, labelX, labelY, 70);
                    
                    // 绘制中间城市的名称
                    drawCityLabelForRouteCities(gc, cityName, positions[0], positions[1], baseRadius, Color.BLACK);
                    
                    // 记录标签位置
                    usedPositions.put(cityName, positions);
                }
            }
        }
    }
    
    /**
     * 为路线上的起点/终点城市绘制标签（不同于普通城市的标签）
     */
    private void drawStartEndCityLabel(GraphicsContext gc, String cityName, double x, double y, Color textColor) {
        // 绘制文本背景
        String displayName = cityName;
        double textWidth = displayName.length() * 7;
        double textHeight = 20;
        
        gc.setFill(Color.WHITE);
        gc.fillRoundRect(x - textWidth/2, 
                         y, 
                         textWidth, textHeight, 5, 5);
        
        // 绘制文本
        gc.setFill(textColor);
        gc.setFont(new Font(gc.getFont().getName(), 12));
        gc.fillText(cityName, x - textWidth/2 + 3, y + 14);
    }
    
    /**
     * 为路线上的中间城市绘制标签
     */
    private void drawCityLabelForRouteCities(GraphicsContext gc, String cityName, double x, double y, double baseRadius, Color textColor) {
        // 绘制文本背景
        String displayName = cityName;
        double textWidth = displayName.length() * 7;
        double textHeight = 18;
        
        gc.setFill(Color.WHITE);
        gc.fillRoundRect(x - textWidth/2, 
                         y + baseRadius * 1.5, 
                         textWidth, textHeight, 5, 5);
        
        // 绘制文本
        gc.setFill(textColor);
        gc.setFont(new Font(gc.getFont().getName(), 11));
        gc.fillText(cityName, x - textWidth/2 + 3, y + baseRadius * 1.5 + 14);
    }
    
    /**
     * 绘制箭头
     */
    private void drawArrow(GraphicsContext gc, double x1, double y1, double x2, double y2) {
        // 计算线段中点
        double midX = (x1 + x2) / 2;
        double midY = (y1 + y2) / 2;
        
        // 计算方向向量
        double dx = x2 - x1;
        double dy = y2 - y1;
        double length = Math.sqrt(dx * dx + dy * dy);
        
        // 规范化方向向量
        double dirX = dx / length;
        double dirY = dy / length;
        
        // 箭头大小
        double arrowSize = 10;
        
        // 计算箭头尾部两点
        double arrowX1 = midX - dirX * arrowSize - dirY * arrowSize / 2;
        double arrowY1 = midY - dirY * arrowSize + dirX * arrowSize / 2;
        double arrowX2 = midX - dirX * arrowSize + dirY * arrowSize / 2;
        double arrowY2 = midY - dirY * arrowSize - dirX * arrowSize / 2;
        
        // 绘制箭头
        gc.setFill(routeColor);
        gc.fillPolygon(
            new double[]{midX, arrowX1, arrowX2},
            new double[]{midY, arrowY1, arrowY2},
            3
        );
    }
    
    /**
     * 绘制距离标签
     */
    private void drawDistanceLabels(GraphicsContext gc, double width, double height) {
        if (optimalRoute.size() < 2) return;
        
        Map<String, City> cityMap = routeService.getCityMap();
        Graph graph = routeService.getGraph();
        
        gc.setFont(new Font("Arial", 12));
        
        for (int i = 0; i < optimalRoute.size() - 1; i++) {
            String city1 = optimalRoute.get(i);
            String city2 = optimalRoute.get(i + 1);
            
            // 获取两个城市之间的距离
            int distance = graph.getDistance(city1, city2);
            
            if (distance > 0) {
                double[] coords1 = cityCoordinates.get(city1);
                double[] coords2 = cityCoordinates.get(city2);
                
                if (coords1 != null && coords2 != null) {
                    double x1 = coords1[0] * width;
                    double y1 = coords1[1] * height;
                    double x2 = coords2[0] * width;
                    double y2 = coords2[1] * height;
                    
                    // 绘制距离标签（在路段中点）
                    double midX = (x1 + x2) / 2;
                    double midY = (y1 + y2) / 2;
                    
                    // 计算标签背景和位置
                    String distanceText = distance + "英里";
                    double textWidth = distanceText.length() * 8;
                    
                    gc.setFill(Color.rgb(255, 255, 255, 0.85));
                    gc.fillRoundRect(midX - textWidth/2, 
                                    midY - 9, 
                                    textWidth, 18, 
                                    9, 9);
                    
                    gc.setStroke(routeColor);
                    gc.setLineWidth(1);
                    gc.strokeRoundRect(midX - textWidth/2, 
                                      midY - 9, 
                                      textWidth, 18, 
                                      9, 9);
                    
                    gc.setFill(Color.BLUE);
                    gc.fillText(distanceText, midX - textWidth/2 + 4, midY + 4);
                }
            }
        }
    }
    
    /**
     * 导出路线到文本文件
     */
    private void exportRoute() {
        if (currentRouteResults == null || currentRouteResults.isEmpty()) {
            showErrorAlert("导出错误", "没有可导出的路线。请先计算路线。");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("导出路线");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("文本文件", "*.txt")
        );
        
        // 设置默认文件名
        String defaultFileName = "Route";
        if (startingCityComboBox.getValue() != null && destinationCityComboBox.getValue() != null) {
            defaultFileName = "Route_" + 
                startingCityComboBox.getValue().replaceAll("\\s+", "_") + 
                "_to_" + 
                destinationCityComboBox.getValue().replaceAll("\\s+", "_");
        }
        fileChooser.setInitialFileName(defaultFileName + ".txt");
        
        File file = fileChooser.showSaveDialog(rootPane.getScene().getWindow());
        
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("=== USA Road Trip Route ===");
                writer.println("From: " + startingCityComboBox.getValue());
                writer.println("To: " + destinationCityComboBox.getValue());
                
                if (!selectedAttractions.isEmpty()) {
                    writer.println("\nVisiting attractions:");
                    for (String attraction : selectedAttractions) {
                        writer.println("- " + attraction);
                    }
                }
                
                writer.println("\n=== Route Details ===");
                for (String line : currentRouteResults) {
                    writer.println(line);
                }
                
                statusLabel.setText("路线已成功导出到: " + file.getName());
            } catch (IOException e) {
                showErrorAlert("导出错误", "导出路线时发生错误: " + e.getMessage());
            }
        }
    }
    
    /**
     * 显示错误提示框
     */
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 