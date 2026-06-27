package br.com.hotel.controller;

import br.com.hotel.model.User;
import br.com.hotel.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    private final UserService userService = new UserService();

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;

    @FXML
    private void handleLogin() {
        String username = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Preencha usuário e senha.");
            return;
        }

        try {
            User user = userService.authenticate(username, password);
            if (user != null) {
                openMainSystem(user);
            } else {
                showError("Usuário ou senha inválidos.");
            }
        } catch (Exception e) {
            showError("Erro ao fazer login.");
            e.printStackTrace();
        }
    }

    private void openMainSystem(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard.fxml"));

            if (loader.getLocation() == null) {
                System.err.println("ERRO: Não encontrou o arquivo /view/dashboard.fxml");
                return;
            }

            Parent root = loader.load();

            Stage stage = (Stage) txtEmail.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Hotel Meia Boca Juniors - Sistema de Gerenciamento");
            stage.setResizable(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erro ao abrir o sistema principal.");
        }
    }

    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
    }
}