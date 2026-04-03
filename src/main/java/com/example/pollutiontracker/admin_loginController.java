package com.example.pollutiontracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class admin_loginController {

    @FXML
    private PasswordField Pass;
    @FXML
    private void IamAdmin(ActionEvent event) throws Exception {

        String passwordText = Pass.getText().trim();
        System.out.println("Admin detected:");
        System.out.println("Password: " + passwordText);

        /*
        Scene scene = new Scene(
                FXMLLoader.load(getClass().getResource("homePage.fxml"))
        );
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();

         */

        clearFields();
    }


    @FXML
    private void returnLogin(ActionEvent event) throws Exception {
        Scene scene = new Scene(
                FXMLLoader.load(getClass().getResource("login_user.fxml"))
        );
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    private void clearFields() {

        Pass.clear();
    }
}
