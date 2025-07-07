@echo off
mkdir bin 2>nul

set JAVAFX_PATH=javafx-sdk-21.0.7\lib

echo 正在编译模型类...
javac -d bin src\main\java\com\roadtrip\model\*.java
if %errorlevel% neq 0 goto error

echo 正在编译服务类...
javac -d bin -cp bin src\main\java\com\roadtrip\service\*.java
if %errorlevel% neq 0 goto error

echo 正在编译算法类...
javac -d bin -cp bin src\main\java\com\roadtrip\algorithm\*.java
if %errorlevel% neq 0 goto error

echo 正在编译工具类...
javac -d bin -cp bin src\main\java\com\roadtrip\util\*.java
if %errorlevel% neq 0 goto error

echo 正在编译控制器类...
javac --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml,javafx.graphics -d bin -cp bin src\main\java\com\roadtrip\gui\*.java
if %errorlevel% neq 0 goto error

echo 所有代码编译成功!
echo 正在复制资源文件...

if not exist bin\com\roadtrip\gui (
    mkdir bin\com\roadtrip\gui
)

copy src\main\resources\com\roadtrip\gui\*.fxml bin\com\roadtrip\gui\
if %errorlevel% neq 0 goto error

echo 资源文件复制完成!
echo 可以运行run.bat来启动应用程序。
goto end

:error
echo 编译失败，请检查错误信息。

:end
pause 