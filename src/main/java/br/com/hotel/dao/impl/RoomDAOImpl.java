package br.com.hotel.dao.impl;

import br.com.hotel.dao.RoomDAO;
import br.com.hotel.model.Room;
import br.com.hotel.model.RoomStatus;
import br.com.hotel.model.RoomType;
import br.com.hotel.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoomDAOImpl implements RoomDAO {

    @Override
    public void save(Room room) {
        String sql = "INSERT INTO rooms (room_number, type, daily_rate, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, room.getRoomNumber());
            stmt.setString(2, room.getType().name());
            stmt.setDouble(3, room.getDailyRate());
            stmt.setString(4, room.getStatus().getDbValue());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        room.setId(rs.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar quarto", e);
        }
    }

    @Override
    public void update(Room room) {
        String sql = "UPDATE rooms SET room_number = ?, type = ?, daily_rate = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, room.getRoomNumber());
            stmt.setString(2, room.getType().name());
            stmt.setDouble(3, room.getDailyRate());
            stmt.setString(4, room.getStatus().getDbValue());
            stmt.setLong(5, room.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar quarto", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM rooms WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar quarto", e);
        }
    }

    @Override
    public Optional<Room> findById(Long id) {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToRoom(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar quarto por ID", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Room> findByNumber(String roomNumber) {
        String sql = "SELECT * FROM rooms WHERE room_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, roomNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToRoom(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar quarto por número", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Room> findAll() {
        String sql = "SELECT * FROM rooms ORDER BY room_number";
        List<Room> rooms = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                rooms.add(mapToRoom(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar quartos", e);
        }
        return rooms;
    }

    @Override
    public List<Room> findByStatus(RoomStatus status) {
        String sql = "SELECT * FROM rooms WHERE status = ? ORDER BY room_number";
        List<Room> rooms = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.getDbValue());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rooms.add(mapToRoom(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar quartos por status", e);
        }
        return rooms;
    }

    @Override
    public List<Room> findByType(RoomType type) {
        String sql = "SELECT * FROM rooms WHERE type = ? ORDER BY room_number";
        List<Room> rooms = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, type.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rooms.add(mapToRoom(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar quartos por tipo", e);
        }
        return rooms;
    }

    private Room mapToRoom(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setId(rs.getLong("id"));
        room.setRoomNumber(rs.getString("room_number"));
        room.setType(RoomType.valueOf(rs.getString("type")));
        room.setDailyRate(rs.getDouble("daily_rate"));
        room.setStatus(RoomStatus.fromString(rs.getString("status")));
        return room;
    }
}