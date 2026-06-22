package br.com.hotel.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;

import java.awt.*;
import java.io.IOException;

/**
 * Controller principal - Gerencia a navegação entre telas dentro do dashboard
 */
public class MainController {

    @FXML private StackPane contentArea;

    private Pane guestsPane;
    private Pane roomsPane;
    private Pane reservationsPane;

    @FXML
    public void initialize() {
        loadDashboardContent();
    }

    private void loadDashboardContent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard-content.fxml"));
            Pane dashboard = loader.load();
            contentArea.getChildren().setAll(dashboard);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openDashboard() {
        loadDashboardContent();
    }

    @FXML
    private void openGuestsScreen() {
        loadContent("/view/guests.fxml", guestsPane, p -> guestsPane = p);
    }

    @FXML
    private void openRoomsScreen() {
        loadContent("/view/rooms.fxml", roomsPane, p -> roomsPane = p);
    }

    @FXML
    private void openReservationsScreen() {
        loadContent("/view/reservations.fxml", reservationsPane, p -> reservationsPane = p);
    }

    private void loadContent(String fxmlPath, Pane cachedPane, java.util.function.Consumer<Pane> setter) {
        try {
            Pane paneToLoad;
            if (cachedPane == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                paneToLoad = loader.load();
                setter.accept(paneToLoad);   // Salva no cache
            } else {
                paneToLoad = cachedPane;
            }

            contentArea.getChildren().setAll(paneToLoad);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erro", "Não foi possível carregar a tela: " + fxmlPath);
        }
    }

    private void showAlert(String title, String message) {
        System.err.println(title + ": " + message);
    }

    @FXML
    private void logout() {
        System.exit(0);
    }
}