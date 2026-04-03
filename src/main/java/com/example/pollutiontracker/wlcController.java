package com.example.pollutiontracker;

import javafx.event.ActionEvent;
import javafx.scene.Node;

public class wlcController extends BaseController {
    @Override
    protected Node getRootNode() {
        return null;
    }

    public void goToLogin(ActionEvent event) {
        switchScene(event, "login_user.fxml");
    }

    public void goToSignUp(ActionEvent event) {
        switchScene(event, "sign_up.fxml");
    }
}