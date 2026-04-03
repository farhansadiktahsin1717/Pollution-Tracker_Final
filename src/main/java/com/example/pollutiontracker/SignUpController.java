package com.example.pollutiontracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.regex.Pattern;

public class SignUpController {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;


    private void switchScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fxmlPath)
            );
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Navigation error.");
        }
    }


    @FXML
    void registerAction() {

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String email = emailField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            messageLabel.setText("Username, Email and Password cannot be empty.");
            return;
        }

        if (username.length() < 3 || username.length() > 50) {
            messageLabel.setText("Username must be between 3 and 50 characters.");
            return;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            messageLabel.setText("Please enter a valid email address.");
            return;
        }

        if (password.length() < 8) {
            messageLabel.setText("Password must be at least 8 characters.");
            return;
        }

        UserDAO.RegistrationResult result = UserDAO.register(username, password, email);
        if (result.isSuccess()) {
            UserSession.setCurrentUserId(result.getUserId());
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(
                        "/com/example/pollutiontracker/UserInfoForm.fxml"));
                Parent root = loader.load();

                UserInfoController controller = loader.getController();
                controller.setUserId(result.getUserId());

                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setMaximized(false);
                stage.setScene(new Scene(root));
                stage.setMaximized(true);

            } catch (Exception e) {
                e.printStackTrace();
                messageLabel.setText("Failed to open user info form.");
            }

        } else {
            messageLabel.setText(result.getMessage());
        }
    }


    @FXML
    void goToLogin(ActionEvent event) {

        switchScene("/com/example/pollutiontracker/login_user.fxml");
    }


}
