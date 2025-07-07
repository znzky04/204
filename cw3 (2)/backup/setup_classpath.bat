@echo off
REM 设置JavaFX SDK路径
set JAVAFX_PATH=C:\javafx\openjfx-24.0.1_windows-x64_bin-sdk\javafx-sdk-24.0.1

REM 复制JavaFX库到项目的lib目录中
echo 为IDE创建JavaFX库引用...

REM 创建lib目录（如果不存在）
if not exist "lib" mkdir lib

REM 复制JavaFX库到lib目录
copy "%JAVAFX_PATH%\lib\javafx.base.jar" "lib\"
copy "%JAVAFX_PATH%\lib\javafx.controls.jar" "lib\"
copy "%JAVAFX_PATH%\lib\javafx.fxml.jar" "lib\"
copy "%JAVAFX_PATH%\lib\javafx.graphics.jar" "lib\"
copy "%JAVAFX_PATH%\lib\javafx.media.jar" "lib\"
copy "%JAVAFX_PATH%\lib\javafx.swing.jar" "lib\"
copy "%JAVAFX_PATH%\lib\javafx.web.jar" "lib\"
copy "%JAVAFX_PATH%\lib\javafx-swt.jar" "lib\"

echo JavaFX库已复制到lib目录
echo.
echo 请在IDE中添加lib目录下的所有jar文件到项目类路径
echo.
echo 在大多数IDE中，你可以:
echo 1. 右键点击项目
echo 2. 选择"Properties"或"Project Structure"
echo 3. 添加lib目录下的所有jar文件到类路径

REM 如果使用的是.iml项目文件，创建或更新.classpath文件
echo ^<?xml version="1.0" encoding="UTF-8"?^> > .classpath
echo ^<classpath^> >> .classpath
echo    ^<classpathentry kind="src" path="src"/^> >> .classpath
echo    ^<classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER"/^> >> .classpath

for %%f in (lib\*.jar) do (
    echo    ^<classpathentry kind="lib" path="%%f"/^> >> .classpath
)

echo ^</classpath^> >> .classpath

echo .classpath文件已更新

echo 完成! 