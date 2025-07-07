package roadtripplanner;

import java.io.File;
import java.lang.reflect.Method;

/**
 * 自动配置JavaFX环境并启动RoadTripPlannerFX的启动程序类
 */
public class RoadTripPlannerLauncher {
    // JavaFX路径常量
    private static final String DEFAULT_JAVAFX_PATH = "C:/javafx/openjfx-24.0.1_windows-x64_bin-sdk/javafx-sdk-24.0.1/lib";
    
    public static void main(String[] args) {
        try {
            // 设置Java工具选项以支持UTF-8
            System.setProperty("file.encoding", "UTF-8");
            
            System.out.println("正在启动Road Trip Planner...");
            
            // 检查是否已经设置了JavaFX模块路径
            boolean javaFxConfigured = checkJavaFxConfigured();
            
            if (!javaFxConfigured) {
                System.out.println("正在配置JavaFX环境...");
                String javafxPath = findJavaFXPath();
                
                if (javafxPath != null) {
                    System.out.println("找到JavaFX库：" + javafxPath);
                    
                    // 设置JavaFX模块路径
                    String modulePath = "--module-path=" + javafxPath;
                    String addModules = "--add-modules=javafx.controls,javafx.fxml,javafx.graphics";
                    
                    // 重新使用正确的模块路径启动程序
                    ProcessBuilder pb = new ProcessBuilder(
                        "java",
                        modulePath.replace("=", " "),  // 分隔成两个参数
                        addModules.replace("=", " "),  // 分隔成两个参数
                        "-cp", ".",  // 当前目录
                        "RoadTripPlannerFX"
                    );
                    
                    pb.inheritIO();  // 继承标准输入输出
                    Process process = pb.start();
                    process.waitFor();  // 等待进程结束
                    System.exit(process.exitValue());
                } else {
                    System.err.println("错误：无法找到JavaFX库文件。");
                    System.err.println("请确保已安装JavaFX SDK，或使用run_fx.bat脚本运行程序。");
                    System.exit(1);
                }
            } else {
                // JavaFX已配置，直接启动应用程序
                Class<?> fxAppClass = Class.forName("RoadTripPlannerFX");
                Method mainMethod = fxAppClass.getMethod("main", String[].class);
                mainMethod.invoke(null, (Object) args);
            }
        } catch (Exception e) {
            System.err.println("启动应用程序时出错：" + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * 检查是否已经配置了JavaFX环境
     */
    private static boolean checkJavaFxConfigured() {
        try {
            // 尝试加载JavaFX类，如果成功则说明已配置
            Class.forName("javafx.application.Application");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    /**
     * 查找JavaFX库的路径
     */
    private static String findJavaFXPath() {
        // 检查默认路径
        File defaultPath = new File(DEFAULT_JAVAFX_PATH);
        if (defaultPath.exists() && defaultPath.isDirectory()) {
            return DEFAULT_JAVAFX_PATH;
        }
        
        // 检查lib目录
        File libDir = new File("lib");
        if (libDir.exists() && libDir.isDirectory()) {
            File[] files = libDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().startsWith("javafx") && file.getName().endsWith(".jar")) {
                        return libDir.getAbsolutePath();
                    }
                }
            }
        }
        
        // 检查环境变量PATH
        String pathEnv = System.getenv("PATH");
        if (pathEnv != null) {
            String[] paths = pathEnv.split(File.pathSeparator);
            for (String path : paths) {
                File dir = new File(path);
                if (dir.exists() && dir.isDirectory()) {
                    File[] files = dir.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            if (file.getName().startsWith("javafx") && file.getName().endsWith(".jar")) {
                                return dir.getAbsolutePath();
                            }
                        }
                    }
                }
            }
        }
        
        return null;
    }
} 