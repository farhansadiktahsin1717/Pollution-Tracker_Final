package com.example.pollutiontracker;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;


public class DashboardController extends BaseController {

    @FXML private Label aqiValueLabel;
    @FXML private Label aqiStatusLabel;


    @Override
    protected Node getRootNode() {
        return aqiValueLabel;
    }


    @FXML
    public void initialize() {
        System.out.println("Dashboard Loaded Successfully");

        // Temporary demo values
        int aqi = 150;
        aqiValueLabel.setText(String.valueOf(aqi));
        aqiStatusLabel.setText(getAqiStatus(aqi));
    }


    private String getAqiStatus(int aqi) {
        if (aqi <= 50)       return "Good";
        else if (aqi <= 100) return "Moderate";
        else if (aqi <= 150) return "Unhealthy for Sensitive Groups";
        else if (aqi <= 200) return "Unhealthy";
        else if (aqi <= 300) return "Very Unhealthy";
        else                 return "Hazardous";
    }


    @FXML
    public void handleHome() {
        switchScene("homePage.fxml");
    }


    @FXML
    public void handleLogout() {
        switchScene("login_user.fxml");
    }
}