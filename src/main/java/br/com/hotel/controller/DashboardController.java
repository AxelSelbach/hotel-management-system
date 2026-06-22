package br.com.hotel.controller;

import br.com.hotel.model.Room;
import br.com.hotel.model.RoomStatus;
import br.com.hotel.service.RoomService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;

import java.util.List;

public class DashboardController {

    private final RoomService roomService = new RoomService();

    @FXML private PieChart occupancyChart;
    @FXML private Label lblTotalRooms;
    @FXML private Label lblOccupied;
    @FXML private Label lblAvailable;
    @FXML private Label lblOccupancyRate;

    @FXML
    public void initialize() {
        loadDashboardData();
    }

    private void loadDashboardData() {
        List<Room> allRooms = roomService.findAll();
        long total = allRooms.size();
        long occupied = allRooms.stream().filter(r -> r.getStatus() == RoomStatus.OCCUPIED).count();
        long available = total - occupied;

        double occupancyRate = total > 0 ? (occupied * 100.0 / total) : 0;

        // Atualiza Labels
        lblTotalRooms.setText("Total de Quartos: " + total);
        lblOccupied.setText("Ocupados: " + occupied);
        lblAvailable.setText("Disponíveis: " + available);
        lblOccupancyRate.setText(String.format("Taxa de Ocupação: %.1f%%", occupancyRate));

        // Gráfico de Pizza
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("Ocupados", occupied),
                new PieChart.Data("Disponíveis", available)
        );

        occupancyChart.setData(pieData);
        occupancyChart.setLabelsVisible(true);
    }
}