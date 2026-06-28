package br.com.hotel.controller;

import br.com.hotel.model.*;
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
    @FXML private TableColumn<Reservation, ReservationStatus> colStatus;

    private final ObservableList<Reservation> reservationList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configureTable();
        loadComboBoxes();
        refreshReservations();
        this.getClass().getSimpleName();

        dpCheckIn.valueProperty().addListener((obs, old, newV) -> calculateTotal());
        dpCheckOut.valueProperty().addListener((obs, old, newV) -> calculateTotal());
    }

    public void refreshData() {
        refreshReservations();
        loadComboBoxes();
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
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Colorir a coluna de Status
        colStatus.setCellFactory(column -> new TableCell<Reservation, ReservationStatus>() {
            @Override
            protected void updateItem(ReservationStatus status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status.getDisplayName());
                    switch (status) {
                        case CHECKED_OUT -> setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                        case CHECKED_IN -> setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
                        case CANCELLED -> setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                        default -> setStyle("-fx-text-fill: #3498db;");
                    }
                }
            }
        });

        tableReservations.setItems(reservationList);
    }

    private void loadComboBoxes() {
        // Hóspedes
        cbGuest.getItems().clear();
        cbGuest.getItems().addAll(guestService.findAll());

        //Quartos DISPONÍVEIS apenas
        cbRoom.getItems().clear();
        cbRoom.getItems().addAll(roomService.findAvailableRooms());

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
    private void createReservation() {
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
            reservation.setStatus(ReservationStatus.CONFIRMED); // ← Confirmada

            reservationService.save(reservation);

            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Reserva criada com sucesso!");
            refreshReservations();
            clearForm();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro", e.getMessage());
        }
    }

    @FXML
    private void performCheckIn() {
        Reservation selected = tableReservations.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aviso", "Selecione uma reserva para fazer Check-In.");
            return;
        }

        if (selected.getStatus() != ReservationStatus.CONFIRMED) {
            showAlert(Alert.AlertType.WARNING, "Aviso", "Só é possível fazer check-in em reservas confirmadas.");
            return;
        }

        try {
            reservationService.checkIn(selected.getId());
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Check-In realizado com sucesso!");
            refreshReservations();
            loadComboBoxes();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro", e.getMessage());
        }
    }

    @FXML
    private void performCheckOut() {
        Reservation selected = tableReservations.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aviso", "Selecione uma reserva para realizar o Check-Out.");
            return;
        }

        // Validações
        if (selected.getStatus() == ReservationStatus.CANCELLED) {
            showAlert(Alert.AlertType.WARNING, "Aviso", "Não é possível fazer Check-Out de uma reserva cancelada.");
            return;
        }

        if (selected.getStatus() == ReservationStatus.CHECKED_OUT) {
            showAlert(Alert.AlertType.WARNING, "Aviso", "Esta reserva já foi finalizada.");
            return;
        }

        if (selected.getStatus() == ReservationStatus.CONFIRMED) {
            showAlert(Alert.AlertType.WARNING, "Aviso",
                    "Esta reserva ainda não teve Check-In. Faça o Check-In primeiro.");
            return;
        }

        try {
            double totalAmount = reservationService.checkOut(selected.getId());
            showAlert(Alert.AlertType.INFORMATION, "Check-Out Concluído",
                    "Estadia finalizada com sucesso!\n\n" +
                            "Hóspede: " + selected.getGuest().getName() + "\n" +
                            "Valor Total: R$ " + String.format("%.2f", totalAmount));

            refreshReservations();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro", e.getMessage());
        }
    }

    @FXML
    private void cancelReservation() {
        Reservation selected = tableReservations.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aviso", "Selecione uma reserva para cancelar.");
            return;
        }

        // Validação: só permite cancelar reservas Confirmadas
        if (selected.getStatus() != ReservationStatus.CONFIRMED) {
            showAlert(Alert.AlertType.WARNING, "Aviso",
                    "Só é possível cancelar reservas que ainda estão 'Confirmadas'.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Cancelamento");
        confirm.setContentText("Deseja realmente cancelar a reserva do hóspede " +
                selected.getGuest().getName() + "?\n\n" +
                "Esta ação não poderá ser desfeita.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Libera o quarto
                    Room room = selected.getRoom();
                    room.setStatus(RoomStatus.AVAILABLE);
                    roomService.update(room);

                    // Atualiza status da reserva
                    selected.setStatus(ReservationStatus.CANCELLED);
                    reservationService.update(selected);  // ou reservationService.update()

                    showAlert(Alert.AlertType.INFORMATION, "Cancelado",
                            "Reserva cancelada com sucesso!");
                    refreshReservations();
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Erro", e.getMessage());
                }
            }
        });
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