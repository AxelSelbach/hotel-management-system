package br.com.hotel.dao.impl;

import br.com.hotel.dao.GuestDAO;
import br.com.hotel.model.Guest;
import br.com.hotel.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GuestDAOImpl implements GuestDAO {

    @Override
    public void save(Guest guest) {
        String sql = "INSERT INTO guests (name, cpf, phone, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, guest.getName());
            stmt.setString(2, guest.getCpf());
            stmt.setString(3, guest.getPhone());
            stmt.setString(4, guest.getEmail());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        guest.setId(rs.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar hóspede", e);
        }
    }

    @Override
    public void update(Guest guest) {
        String sql = "UPDATE guests SET name = ?, cpf = ?, phone = ?, email = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, guest.getName());
            stmt.setString(2, guest.getCpf());
            stmt.setString(3, guest.getPhone());
            stmt.setString(4, guest.getEmail());
            stmt.setLong(5, guest.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar hóspede", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM guests WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar hóspede", e);
        }
    }

    @Override
    public Optional<Guest> findById(Long id) {
        String sql = "SELECT * FROM guests WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToGuest(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar hóspede por ID", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Guest> findByCpf(String cpf) {
        String sql = "SELECT * FROM guests WHERE cpf = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToGuest(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar hóspede por CPF", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Guest> findAll() {
        String sql = "SELECT * FROM guests ORDER BY name";
        List<Guest> guests = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                guests.add(mapToGuest(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar hóspedes", e);
        }
        return guests;
    }

    @Override
    public List<Guest> findByName(String name) {
        String sql = "SELECT * FROM guests WHERE name ILIKE ? ORDER BY name";
        List<Guest> guests = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + name + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    guests.add(mapToGuest(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar hóspedes por nome", e);
        }
        return guests;
    }

    private Guest mapToGuest(ResultSet rs) throws SQLException {
        Guest guest = new Guest();
        guest.setId(rs.getLong("id"));
        guest.setName(rs.getString("name"));
        guest.setCpf(rs.getString("cpf"));
        guest.setPhone(rs.getString("phone"));
        guest.setEmail(rs.getString("email"));
        return guest;
    }
}