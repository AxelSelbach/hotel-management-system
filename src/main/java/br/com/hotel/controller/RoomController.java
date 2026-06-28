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

import java.util.List;

public class RoomController {

    private final RoomService roomService = new RoomService();
    private final ObservableList<Room> roomList = FXCollections.observableArrayList();

    @FXML private TableView<Room> tableRooms;
    @FXML private TableColumn<Room, Long> colId;
    @FXML private TableColumn<Room, String> colNumber;
    @FXML private TableColumn<Room, RoomType> colType;
    @FXML private TableColumn<Room, RoomStatus> colStatus;
    @FXML private TableColumn<Room, Double> colPrice;

    // Formulário
    @FXML private TextField txtNumber;
    @FXML private ComboBox<RoomType> cbTypeForm;
    @FXML private TextField txtPrice;

    // Filtros
    @FXML private ComboBox<RoomType> cbType;
    @FXML private ComboBox<RoomStatus> cbStatus;

    private Room selectedRoom = null;

    @FXML
    public void initialize() {
        configureTable();
        loadRooms();
        loadComboBoxes();
        this.getClass().getSimpleName();
    }

    public void refreshData() {
        loadRooms();
    }

    private void configureTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNumber.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("dailyRate"));

        tableRooms.setItems(roomList);

        // Duplo clique para editar
        tableRooms.setRowFactory(tv -> {
            TableRow<Room> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    editSelectedRoom();
                }
            });
            return row;
        });
    }

    private void loadRooms() {
        roomList.clear();
        roomList.addAll(roomService.findAll());
    }

    private void loadComboBoxes() {
        cbTypeForm.getItems().addAll(RoomType.values());
        cbType.getItems().addAll(RoomType.values());
        cbStatus.getItems().addAll(RoomStatus.values());
    }

    @FXML
    private void saveRoom() {
        try {
            // Validações
            if (txtNumber.getText().trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Aviso", "O número do quarto é obrigatório!");
                return;
            }
            if (cbTypeForm.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Aviso", "Selecione o tipo do quarto!");
                return;
            }
            if (txtPrice.getText().trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Aviso", "Informe o valor da diária!");
                return;
            }

            double dailyRate;
            try {
                dailyRate = Double.parseDouble(txtPrice.getText().trim());
                if (dailyRate <= 0) {
                    showAlert(Alert.AlertType.WARNING, "Aviso", "O valor da diária deve ser maior que zero!");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Aviso", "Valor da diária inválido! Use apenas números (ex: 150.00)");
                return;
            }

            Room room = new Room();
            room.setRoomNumber(txtNumber.getText().trim());
            room.setType(cbTypeForm.getValue());
            room.setDailyRate(dailyRate);
            room.setStatus(RoomStatus.AVAILABLE);

            if (selectedRoom == null) {
                roomService.save(room);
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Quarto cadastrado com sucesso!");
            } else {
                room.setId(selectedRoom.getId());
                roomService.update(room);
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Quarto atualizado com sucesso!");
            }

            clearForm();
            selectedRoom = null;
            // Teste temporário
            System.out.println("Tipo salvo no banco: " + room.getType().getDbValue());
            loadRooms();

        } catch (Exception e) {
            e.printStackTrace(); // Isso vai mostrar o erro real no console
            showAlert(Alert.AlertType.ERROR, "Erro ao salvar quarto",
                    e.getMessage() != null ? e.getMessage() : "Erro desconhecido. Verifique o console.");
        }
    }

    @FXML
    private void newRoom() {
        clearForm();
        selectedRoom = null;
    }

    @FXML
    private void editRoom() {
        editSelectedRoom();
    }

    private void editSelectedRoom() {
        Room selected = tableRooms.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aviso", "Selecione um quarto para editar.");
            return;
        }

        selectedRoom = selected;
        txtNumber.setText(selected.getRoomNumber());
        cbTypeForm.setValue(selected.getType());
        txtPrice.setText(String.valueOf(selected.getDailyRate()));
    }

    @FXML
    private void deleteRoom() {
        Room selected = tableRooms.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aviso", "Selecione um quarto para excluir.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Exclusão");
        confirm.setContentText("Deseja realmente excluir o quarto " + selected.getRoomNumber() + "?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    roomService.delete(selected.getId());
                    loadRooms();
                    showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Quarto excluído com sucesso!");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Erro", e.getMessage());
                }
            }
        });
    }

    @FXML
    public void filterRooms() {
        try {
            RoomType selectedType = cbType.getValue();
            RoomStatus selectedStatus = cbStatus.getValue();

            List<Room> filtered = roomService.findWithFilters(selectedType, selectedStatus);

            roomList.clear();
            roomList.addAll(filtered);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível aplicar os filtros.");
        }
    }

    @FXML
    private void clearFilters() {
        cbType.setValue(null);
        cbStatus.setValue(null);
        loadRooms();
    }

    private void clearForm() {
        txtNumber.clear();
        txtPrice.clear();
        cbTypeForm.setValue(null);
        selectedRoom = null;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}