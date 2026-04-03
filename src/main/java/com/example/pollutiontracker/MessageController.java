package com.example.pollutiontracker;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MessageController extends BaseController {

    @FXML private Label dateLabel;

    @Override
    protected Node getRootNode() {
        return dateLabel;
    }

    @FXML
    public void initialize() {
        LocalDate today = LocalDate.now();
        dateLabel.setText(today.format(DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy")));
    }


    @FXML public void handleHome()             { switchScene("homePage.fxml"); }
    @FXML public void handleTreeEstimation()   { switchScene("TreeEstimation.fxml"); }
    @FXML public void handleProfile()          { switchScene("Profile.fxml"); }
}
