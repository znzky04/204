package com.roadtrip.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * 路线规划系统的JavaFX应用程序入口
 */
public class RoadTripFXApp extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // 加载FXML布局文件
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/roadtrip/gui/RoadTripView.fxml"));
        Parent root = loader.load();
        
        // 创建场景并设置最小尺寸
        Scene scene = new Scene(root, 1200, 800);
        
        // 设置场景和窗口属性
        primaryStage.setTitle("USA Road Trip Planner");
        primaryStage.setScene(scene);
        
        // 允许调整窗口大小
        primaryStage.setResizable(true);
        
        // 设置最小窗口尺寸
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(700);
        
        // 最大化窗口
        primaryStage.setMaximized(true);
        
        // 显示窗口
        primaryStage.show();
    }
    
    /**
     * 应用程序入口
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        launch(args);
    }
} 