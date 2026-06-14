package br.com.hotel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Carrega a tela principal (dashboard)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard.fxml"));

            Scene scene = new Scene(loader.load());

            // Configurações da janela principal
            primaryStage.setTitle("Hotel Meia Boca Juniors - Sistema de Gerenciamento");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(650);
            primaryStage.setResizable(true);

            primaryStage.setMaximized(true);

            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao iniciar a aplicação: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}