@echo off
set JAVAFX_PATH=javafx-sdk-21.0.7\lib
java --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp bin com.roadtrip.gui.RoadTripFXApp
pause 