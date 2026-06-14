package br.com.hotel.controller;

import br.com.hotel.model.Room;
import br.com.hotel.model.RoomType;
import br.com.hotel.model.RoomStatus;
import br.com.hotel.service.RoomService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class RoomController {

    private final RoomService roomService = new RoomService();
    private final ObservableList<Room> roomList = FXCollections.observableArrayList();

    @FXML private TableView<Room> tableRooms;
    @FXML private TableColumn<Room, Long> colId;
    @FXML private TableColumn<Room, String> colNumber, colType, colStatus;
    @FXML private TableColumn<Room, Double> colPrice;

    @FXML private TextField txtNumber, txtPrice;
    @FXML private ComboBox<RoomType> cbTypeForm;
    @FXML private ComboBox<RoomStatus> cbStatus;

    @FXML
    public void initialize() {
        configureTable();
        loadRooms();
        loadComboBoxes();
    }

    private void configureTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNumber.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("dailyRate"));
    }

    private void loadRooms() {
        roomList.clear();
        roomList.addAll(roomService.findAll());
        tableRooms.setItems(roomList);
    }

    private void loadComboBoxes() {
        cbTypeForm.getItems().addAll(RoomType.values());
    }

    @FXML
    private void saveRoom() {
        try {
            Room room = new Room();
            room.setRoomNumber(txtNumber.getText());
            room.setType(cbTypeForm.getValue());
            room.setDailyRate(Double.parseDouble(txtPrice.getText()));
            room.setStatus(RoomStatus.AVAILABLE);

            roomService.save(room);
            loadRooms();
            clearForm();
            showAlert("Sucesso", "Quarto cadastrado!");
        } catch (Exception e) {
            showAlert("Erro", e.getMessage());
        }
    }

    private void clearForm() {
        txtNumber.clear();
        txtPrice.clear();
        cbTypeForm.setValue(null);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}