@echo off
echo 正在复制FXML和其他资源文件...

if not exist bin\com\roadtrip\gui (
    mkdir bin\com\roadtrip\gui
)

copy src\main\resources\com\roadtrip\gui\*.fxml bin\com\roadtrip\gui\

echo 资源文件复制完成!
pause 