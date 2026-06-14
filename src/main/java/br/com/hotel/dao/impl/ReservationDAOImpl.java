package br.com.hotel.dao.impl;

import br.com.hotel.dao.ReservationDAO;
import br.com.hotel.model.*;
import br.com.hotel.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservationDAOImpl implements ReservationDAO {

    @Override
    public void save(Reservation reservation) {
        String sql = """
            INSERT INTO reservations 
            (guest_id, room_id, check_in_date, check_out_date, total_amount) 
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, reservation.getGuest().getId());
            stmt.setLong(2, reservation.getRoom().getId());
            stmt.setDate(3, Date.valueOf(reservation.getCheckInDate()));
            stmt.setDate(4, Date.valueOf(reservation.getCheckOutDate()));
            stmt.setDouble(5, reservation.getTotalAmount());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        reservation.setId(rs.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar reserva", e);
        }
    }

    @Override
    public void update(Reservation reservation) {
        String sql = """
            UPDATE reservations SET 
            guest_id = ?, room_id = ?, check_in_date = ?, 
            check_out_date = ?, total_amount = ? 
            WHERE id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, reservation.getGuest().getId());
            stmt.setLong(2, reservation.getRoom().getId());
            stmt.setDate(3, Date.valueOf(reservation.getCheckInDate()));
            stmt.setDate(4, Date.valueOf(reservation.getCheckOutDate()));
            stmt.setDouble(5, reservation.getTotalAmount());
            stmt.setLong(6, reservation.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar reserva", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM reservations WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar reserva", e);
        }
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        String sql = """
            SELECT r.*, g.*, rm.* 
            FROM reservations r
            JOIN guests g ON r.guest_id = g.id
            JOIN rooms rm ON r.room_id = rm.id
            WHERE r.id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToReservation(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar reserva por ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Reservation> findAll() {
        String sql = """
            SELECT r.*, g.*, rm.* 
            FROM reservations r
            JOIN guests g ON r.guest_id = g.id
            JOIN rooms rm ON r.room_id = rm.id
            ORDER BY r.check_in_date DESC
            """;

        List<Reservation> reservations = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                reservations.add(mapToReservation(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar reservas", e);
        }
        return reservations;
    }

    @Override
    public List<Reservation> findByGuestId(Long guestId) {
        String sql = """
            SELECT r.*, g.*, rm.* 
            FROM reservations r
            JOIN guests g ON r.guest_id = g.id
            JOIN rooms rm ON r.room_id = rm.id
            WHERE r.guest_id = ?
            ORDER BY r.check_in_date DESC
            """;

        List<Reservation> reservations = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, guestId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapToReservation(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar reservas por hóspede", e);
        }
        return reservations;
    }

    @Override
    public List<Reservation> findByRoomId(Long roomId) {
        // Implementação similar ao findByGuestId...
        // Pode ser implementada conforme necessidade
        return new ArrayList<>();
    }

    @Override
    public List<Reservation> findByCheckInDateRange(LocalDate start, LocalDate end) {
        String sql = """
            SELECT r.*, g.*, rm.* 
            FROM reservations r
            JOIN guests g ON r.guest_id = g.id
            JOIN rooms rm ON r.room_id = rm.id
            WHERE r.check_in_date BETWEEN ? AND ?
            ORDER BY r.check_in_date
            """;

        List<Reservation> reservations = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(start));
            stmt.setDate(2, Date.valueOf(end));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapToReservation(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar reservas por período", e);
        }
        return reservations;
    }

    // OPERAÇÕES ESPECÍFICAS DO HOTEL

    @Override
    public boolean checkIn(Long reservationId) {
        String sql = "UPDATE rooms SET status = 'occupied' WHERE id = (SELECT room_id FROM reservations WHERE id = ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, reservationId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao realizar check-in", e);
        }
    }

    @Override
    public boolean checkOut(Long reservationId) {
        String updateRoomSql = """
            UPDATE rooms SET status = 'available' 
            WHERE id = (SELECT room_id FROM reservations WHERE id = ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt1 = conn.prepareStatement(updateRoomSql)) {

            stmt1.setLong(1, reservationId);
            stmt1.executeUpdate();
            return true;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao realizar check-out", e);
        }
    }

    private Reservation mapToReservation(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();

        // Dados da Reserva
        reservation.setId(rs.getLong("id"));
        reservation.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
        reservation.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());
        reservation.setTotalAmount(rs.getDouble("total_amount"));

        // Dados do Hóspede
        Guest guest = new Guest();
        guest.setId(rs.getLong("guest_id"));
        guest.setName(rs.getString("name"));
        guest.setCpf(rs.getString("cpf"));
        guest.setPhone(rs.getString("phone"));
        guest.setEmail(rs.getString("email"));
        reservation.setGuest(guest);

        // Dados do Quarto
        Room room = new Room();
        room.setId(rs.getLong("room_id"));
        room.setRoomNumber(rs.getString("room_number"));
        room.setType(RoomType.valueOf(rs.getString("type")));
        room.setDailyRate(rs.getDouble("daily_rate"));
        room.setStatus(RoomStatus.fromString(rs.getString("status")));
        reservation.setRoom(room);

        return reservation;
    }
}