package br.com.hotel.controller;

import br.com.hotel.model.Guest;
import br.com.hotel.model.Reservation;
import br.com.hotel.model.Room;
import br.com.hotel.service.GuestService;
import br.com.hotel.service.ReservationService;
import br.com.hotel.service.RoomService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;

public class ReservationController {

    private final ReservationService reservationService = new ReservationService();
    private final GuestService guestService = new GuestService();
    private final RoomService roomService = new RoomService();

    @FXML private ComboBox<Guest> cbGuest;
    @FXML private ComboBox<Room> cbRoom;
    @FXML private DatePicker dpCheckIn;
    @FXML private DatePicker dpCheckOut;
    @FXML private Label lblTotal;

    @FXML private TableView<Reservation> tableReservations;
    @FXML private TableColumn<Reservation, Long> colId;
    @FXML private TableColumn<Reservation, String> colGuest;
    @FXML private TableColumn<Reservation, String> colRoom;
    @FXML private TableColumn<Reservation, LocalDate> colCheckIn;
    @FXML private TableColumn<Reservation, LocalDate> colCheckOut;
    @FXML private TableColumn<Reservation, Double> colTotal;

    private final ObservableList<Reservation> reservationList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configureTable();
        loadComboBoxes();
        refreshReservations();

        // Atualiza valor total automaticamente
        dpCheckIn.valueProperty().addListener((obs, old, newV) -> calculateTotal());
        dpCheckOut.valueProperty().addListener((obs, old, newV) -> calculateTotal());
    }

    private void configureTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colGuest.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().getGuest().getName()));
        colRoom.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty("Quarto " + cell.getValue().getRoom().getRoomNumber()));
        colCheckIn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        colCheckOut.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        tableReservations.setItems(reservationList);
    }

    private void loadComboBoxes() {
        // Hóspedes
        cbGuest.getItems().clear();
        cbGuest.getItems().addAll(guestService.findAll());

        cbGuest.setConverter(new javafx.util.StringConverter<Guest>() {
            @Override
            public String toString(Guest guest) {
                if (guest == null) return "";
                return guest.getName() + " (" + guest.getCpf() + ")";
            }

            @Override
            public Guest fromString(String string) {
                return null;
            }
        });

        // Quartos
        cbRoom.getItems().clear();
        cbRoom.getItems().addAll(roomService.findAvailableRooms());

        cbRoom.setConverter(new javafx.util.StringConverter<Room>() {
            @Override
            public String toString(Room room) {
                if (room == null) return "";
                return "Quarto " + room.getRoomNumber() + " - " + room.getType();
            }

            @Override
            public Room fromString(String string) {
                return null;
            }
        });
    }

    @FXML
    private void calculateTotal() {
        if (cbRoom.getValue() == null || dpCheckIn.getValue() == null || dpCheckOut.getValue() == null) {
            lblTotal.setText("R$ 0,00");
            return;
        }

        try {
            long days = java.time.temporal.ChronoUnit.DAYS.between(dpCheckIn.getValue(), dpCheckOut.getValue());
            if (days > 0) {
                double total = days * cbRoom.getValue().getDailyRate();
                lblTotal.setText(String.format("R$ %.2f", total));
            } else {
                lblTotal.setText("R$ 0,00");
            }
        } catch (Exception e) {
            lblTotal.setText("R$ 0,00");
        }
    }

    @FXML
    private void refreshReservations() {
        reservationList.clear();
        reservationList.addAll(reservationService.findAll());
    }

    @FXML
    private void performCheckIn() {
        try {
            if (cbGuest.getValue() == null || cbRoom.getValue() == null ||
                    dpCheckIn.getValue() == null || dpCheckOut.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Aviso", "Preencha todos os campos!");
                return;
            }

            Reservation reservation = new Reservation();
            reservation.setGuest(cbGuest.getValue());
            reservation.setRoom(cbRoom.getValue());
            reservation.setCheckInDate(dpCheckIn.getValue());
            reservation.setCheckOutDate(dpCheckOut.getValue());

            reservationService.save(reservation);
            reservationService.checkIn(reservation.getId());

            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Check-In realizado com sucesso!");
            refreshReservations();
            clearForm();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro", e.getMessage());
        }
    }

    @FXML
    private void performCheckOut() {
        Reservation selected = tableReservations.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aviso", "Selecione uma reserva para fazer Check-Out.");
            return;
        }

        try {
            double total = reservationService.checkOut(selected.getId());
            showAlert(Alert.AlertType.INFORMATION, "Check-Out",
                    "Check-Out realizado!\nValor total: R$ " + String.format("%.2f", total));
            refreshReservations();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro", e.getMessage());
        }
    }

    @FXML
    private void cancelReservation() {
        clearForm();
    }

    private void clearForm() {
        cbGuest.setValue(null);
        cbRoom.setValue(null);
        dpCheckIn.setValue(null);
        dpCheckOut.setValue(null);
        lblTotal.setText("R$ 0,00");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}