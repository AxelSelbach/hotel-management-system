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
    @FXML private TableColumn<Room, String> colNumber;
    @FXML private TableColumn<Room, RoomType> colType;
    @FXML private TableColumn<Room, RoomStatus> colStatus;
    @FXML private TableColumn<Room, Double> colPrice;

    @FXML private ComboBox<RoomType> cbType;
    @FXML private ComboBox<RoomStatus> cbStatus;
    @FXML private TextField txtNumber;
    @FXML private TextField txtPrice;
    @FXML private ComboBox<RoomType> cbTypeForm;

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

        tableRooms.setItems(roomList);
    }

    private void loadRooms() {
        roomList.clear();
        roomList.addAll(roomService.findAll());
    }

    private void loadComboBoxes() {
        cbType.getItems().addAll(RoomType.values());
        cbTypeForm.getItems().addAll(RoomType.values());
        cbStatus.getItems().addAll(RoomStatus.values());
    }

    @FXML
    private void filterRooms() {
        // Implementação futura de filtro
        loadRooms(); // por enquanto recarrega todos
    }

    @FXML
    private void clearFilters() {
        cbType.setValue(null);
        cbStatus.setValue(null);
        loadRooms();
    }

    @FXML
    private void saveRoom() {
        try {
            Room room = new Room();
            room.setRoomNumber(txtNumber.getText().trim());
            room.setType(cbTypeForm.getValue());
            room.setDailyRate(Double.parseDouble(txtPrice.getText().trim()));
            room.setStatus(RoomStatus.AVAILABLE);

            roomService.save(room);
            loadRooms();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Quarto cadastrado com sucesso!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro", e.getMessage());
        }
    }

    @FXML
    private void editRoom() {
        showAlert(Alert.AlertType.INFORMATION, "Aviso", "Funcionalidade de edição em desenvolvimento.");
    }

    @FXML
    private void deleteRoom() {
        Room selected = tableRooms.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aviso", "Selecione um quarto para excluir.");
            return;
        }
        roomService.delete(selected.getId());
        loadRooms();
        showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Quarto excluído!");
    }

    private void clearForm() {
        txtNumber.clear();
        txtPrice.clear();
        cbTypeForm.setValue(null);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}