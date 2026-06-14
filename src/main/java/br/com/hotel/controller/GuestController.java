package br.com.hotel.controller;

import br.com.hotel.model.Guest;
import br.com.hotel.service.GuestService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class GuestController {

    private final GuestService guestService = new GuestService();
    private final ObservableList<Guest> guestList = FXCollections.observableArrayList();

    @FXML private TextField txtCpfSearch;
    @FXML private TableView<Guest> tableGuests;
    @FXML private TableColumn<Guest, Long> colID;
    @FXML private TableColumn<Guest, String> colName, colCPF, colPhone, colEmail;

    @FXML private Button btnNew, btnEdit, btnDelete;

    @FXML
    public void initialize() {
        configureTable();
        loadAllGuests();
    }

    private void configureTable() {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCPF.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        tableGuests.setItems(guestList);
    }

    @FXML
    private void loadAllGuests() {
        guestList.clear();
        guestList.addAll(guestService.findAll());
    }

    @FXML
    private void searchGuest() {
        String cpf = txtCpfSearch.getText().trim();
        guestList.clear();
        if (cpf.isEmpty()) {
            guestList.addAll(guestService.findAll());
        } else {
            guestService.findByCpf(cpf).ifPresent(guestList::add);
        }
    }

    @FXML
    private void newGuest() {
        showAlert("Info", "Funcionalidade de Novo Hóspede em desenvolvimento.");
    }

    @FXML
    private void editGuest() {
        Guest selected = tableGuests.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Aviso", "Selecione um hóspede para editar.");
            return;
        }
        showAlert("Info", "Edição de hóspede em desenvolvimento.");
    }

    @FXML
    private void deleteGuest() {
        Guest selected = tableGuests.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Aviso", "Selecione um hóspede para excluir.");
            return;
        }
        guestService.delete(selected.getId());
        loadAllGuests();
        showAlert("Sucesso", "Hóspede excluído com sucesso.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}