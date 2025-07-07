@echo off
echo 正在编译JavaFX应用程序...
javac -d bin -cp bin;javafx-sdk-21.0.7\lib\* src\main\java\com\roadtrip\gui\*.java
if %errorlevel% equ 0 (
    echo 编译成功!
) else (
    echo 编译失败，请检查错误信息。
)
pause 