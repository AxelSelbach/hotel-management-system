package br.com.hotel.controller;

import br.com.hotel.model.Reservation;
import br.com.hotel.service.ReservationService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;

public class ReservationController {

    private final ReservationService reservationService = new ReservationService();

    @FXML private ComboBox<?> cbGuest;
    @FXML private ComboBox<?> cbRoom;
    @FXML private DatePicker dpCheckIn;
    @FXML private DatePicker dpCheckOut;
    @FXML private Label lblTotal;

    @FXML
    private void performCheckIn() {
        // Implementação futura
        showAlert("Info", "Check-In em desenvolvimento.");
    }

    @FXML
    private void performCheckOut() {
        showAlert("Info", "Check-Out em desenvolvimento.");
    }

    @FXML
    private void cancelReservation() {
        clearForm();
    }

    private void clearForm() {
        dpCheckIn.setValue(null);
        dpCheckOut.setValue(null);
        lblTotal.setText("R$ 0,00");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}