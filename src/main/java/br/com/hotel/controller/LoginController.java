package br.com.hotel.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;

    @FXML
    private void handleLogin() {
        if ("admin@hotel.com".equals(txtEmail.getText()) && "123456".equals(txtPassword.getText())) {
            showAlert("Sucesso", "Login realizado com sucesso!");
        } else {
            lblError.setText("E-mail ou senha inválidos.");
            lblError.setVisible(true);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}