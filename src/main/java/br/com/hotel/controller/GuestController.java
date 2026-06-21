package br.com.hotel.controller;

import br.com.hotel.model.Guest;
import br.com.hotel.service.GuestService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;

public class GuestController {

    private final GuestService guestService = new GuestService();
    private final ObservableList<Guest> guestList = FXCollections.observableArrayList();

    @FXML private TextField txtName;
    @FXML private TextField txtCpf;
    @FXML private TextField txtPhone;
    @FXML private TextField txtEmail;
    @FXML private TextField txtCpfSearch;

    @FXML private TableView<Guest> tableGuests;
    @FXML private TableColumn<Guest, Long> colID;
    @FXML private TableColumn<Guest, String> colName;
    @FXML private TableColumn<Guest, String> colCPF;
    @FXML private TableColumn<Guest, String> colPhone;
    @FXML private TableColumn<Guest, String> colEmail;

    private Guest selectedGuest = null;

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

        // Clique na tabela para editar
        tableGuests.setRowFactory(tv -> {
            TableRow<Guest> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    editSelectedGuest();
                }
            });
            return row;
        });
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
            loadAllGuests();
        } else {
            guestService.findByCpf(cpf).ifPresent(guestList::add);
        }
    }

    @FXML
    private void newGuest() {
        clearForm();
        selectedGuest = null;
        txtName.requestFocus(); // comentado para evitar erro se o campo não existir ainda
    }

    @FXML
    private void saveGuest() {
        try {
            if (txtName.getText().trim().isEmpty() || txtCpf.getText().trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Aviso", "Nome e CPF são obrigatórios!");
                return;
            }

            Guest guest = new Guest();
            guest.setName(txtName.getText().trim());
            guest.setCpf(txtCpf.getText().trim());
            guest.setPhone(txtPhone.getText().trim());
            guest.setEmail(txtEmail.getText().trim());

            if (selectedGuest == null) {
                // Novo cadastro
                guestService.save(guest);
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Hóspede cadastrado com sucesso!");
            } else {
                // Edição
                guest.setId(selectedGuest.getId());
                guestService.update(guest);
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Hóspede atualizado com sucesso!");
            }

            clearForm();
            selectedGuest = null;
            loadAllGuests();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro", e.getMessage());
        }
    }

    @FXML
    private void editGuest() {
        editSelectedGuest();
    }

    private void editSelectedGuest() {
        Guest selected = tableGuests.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aviso", "Selecione um hóspede para editar.");
            return;
        }

        selectedGuest = selected;
        txtName.setText(selected.getName());
        txtCpf.setText(selected.getCpf());
        txtPhone.setText(selected.getPhone() != null ? selected.getPhone() : "");
        txtEmail.setText(selected.getEmail() != null ? selected.getEmail() : "");
    }

    @FXML
    private void deleteGuest() {
        Guest selected = tableGuests.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aviso", "Selecione um hóspede para excluir.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Exclusão");
        confirm.setContentText("Deseja realmente excluir o hóspede " + selected.getName() + "?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    guestService.delete(selected.getId());
                    loadAllGuests();
                    showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Hóspede excluído com sucesso!");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Erro", e.getMessage());
                }
            }
        });
    }

    private void clearForm() {
        if (txtName != null) txtName.clear();
        if (txtCpf != null) txtCpf.clear();
        if (txtPhone != null) txtPhone.clear();
        if (txtEmail != null) txtEmail.clear();
        selectedGuest = null;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}