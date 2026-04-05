package com.example.pollutiontracker;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class TreeEstimationController extends BaseController {

    @FXML private Label dateLabel;
    @FXML private Label treesPerPersonLabel;
    @FXML private Label treesPerAreaLabel;
    @FXML private Label totalTreesLabel;
    @FXML private Label cityTitleLabel;
    @FXML private Label aqiBadge; // Added to make the badge dynamic
    @FXML private ProgressBar personProgress;
    @FXML private ProgressBar areaProgress;

    @Override
    protected Node getRootNode() {
        return dateLabel;
    }

    private static class CityData {
        double area; // km²
        int population;
        CityData(double area, int population) {
            this.area = area;
            this.population = population;
        }
    }

    private static final Map<String, CityData> cityMap = new HashMap<>();

    static {
        cityMap.put("Dhaka", new CityData(306.4, 10000000));
        cityMap.put("Chittagong", new CityData(168.07, 5000000));
        cityMap.put("Khulna", new CityData(59.57, 1500000));
        cityMap.put("Rajshahi", new CityData(96.69, 800000));
        cityMap.put("Sylhet", new CityData(26.5, 700000));
        cityMap.put("Barisal", new CityData(58.0, 500000));
        cityMap.put("Rangpur", new CityData(205.0, 800000));
        cityMap.put("Mymensingh", new CityData(91.3, 600000));
    }

    @FXML
    public void initialize() {
        // 1. Set Date
        LocalDate today = LocalDate.now();
        dateLabel.setText(today.format(DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy")));

        // 2. Get Session Data
        String city = UserSession.getCity();
        int aqi = UserSession.getAqi();

        System.out.println(city);
        System.out.println(city);

        // Fallback for null/empty city
        if (city == null || city.isEmpty() || !cityMap.containsKey(city)) {
            city = "Dhaka";
        }

        CityData data = cityMap.get(city);

        // 3. Dynamic UI Updates
        cityTitleLabel.setText("Tree Plantation Estimation — " + city);
        updateAqiBadge(aqi);

        // 4. Calculations
        // Target: 1 tree for every 25 AQI points (e.g., AQI 200 needs 8 trees)
        int targetTreesPerPerson = Math.max(2, aqi / 25);
        double treesPerAreaTarget = (double) data.population / data.area;
        double totalTreesNeeded = (double) targetTreesPerPerson * data.population;

        // 5. Update Labels
        treesPerPersonLabel.setText(String.valueOf(targetTreesPerPerson));
        treesPerAreaLabel.setText(String.format("%,.0f", treesPerAreaTarget));
        totalTreesLabel.setText(String.format("%.1fM", totalTreesNeeded / 1_000_000));

        // 6. Dynamic Progress (Simulation of current vs target)
        // Assuming current average is 2.4 trees per person for logic
        double currentAvg = 2.4;
        personProgress.setProgress(Math.min(1.0, currentAvg / targetTreesPerPerson));
        areaProgress.setProgress(0.4); // Sample static progress for area
    }

    private void updateAqiBadge(int aqi) {
        String status;
        String style;
        if (aqi <= 50) { status = "Good"; style = "health-badge-green"; }
        else if (aqi <= 100) { status = "Moderate"; style = "health-badge-yellow"; }
        else if (aqi <= 200) { status = "Unhealthy"; style = "health-badge-red"; }
        else { status = "Hazardous"; style = "health-badge-purple"; }

        aqiBadge.setText("AQI: " + aqi + " — " + status);
        aqiBadge.getStyleClass().removeAll("health-badge-red", "health-badge-green", "health-badge-yellow");
        aqiBadge.getStyleClass().add(style);
    }

    @FXML public void handleHome() { switchScene("homePage.fxml"); }
    @FXML public void handleMessage() { switchScene("Message.fxml"); }
    @FXML public void handleProfile() { switchScene("Profile.fxml"); }
}