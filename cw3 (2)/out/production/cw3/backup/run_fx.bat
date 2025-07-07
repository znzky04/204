@echo off
REM Set Java encoding to UTF-8
set JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8

REM Set JavaFX SDK path
set JAVAFX_PATH=C:\javafx\openjfx-24.0.1_windows-x64_bin-sdk\javafx-sdk-24.0.1

REM Create bin directory (if it doesn't exist)
if not exist "bin" mkdir bin

REM Compile Java files with JavaFX modules
javac -d bin --module-path "%JAVAFX_PATH%\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp ".;%JAVAFX_PATH%\lib\*" RoadTripPlannerFX.java

REM If compilation successful, run the program
if %errorlevel% equ 0 (
    echo Compilation successful, launching application...
    java --module-path "%JAVAFX_PATH%\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp "bin;.;%JAVAFX_PATH%\lib\*" RoadTripPlannerFX
) else (
    echo Compilation failed, please check error messages.
) 