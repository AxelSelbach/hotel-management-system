package br.com.hotel.service;

import br.com.hotel.dao.RoomDAO;
import br.com.hotel.dao.impl.RoomDAOImpl;
import br.com.hotel.model.Room;
import br.com.hotel.model.RoomStatus;
import br.com.hotel.model.RoomType;

import java.util.List;
import java.util.Optional;

/**
 * Service responsável pelas regras de negócio relacionadas aos quartos.
 * Contém validações, cadastro e consultas de disponibilidade.
 */
public class RoomService {

    private final RoomDAO roomDAO;

    public RoomService() {
        this.roomDAO = new RoomDAOImpl();
    }

    //Valida os campos obrigatórios de um quarto
    private void validateRoom(Room room) {
        if (room.getRoomNumber() == null || room.getRoomNumber().trim().isEmpty()) {
            throw new RuntimeException("Número do quarto é obrigatório.");
        }
        if (room.getDailyRate() <= 0) {
            throw new RuntimeException("Valor da diária deve ser maior que zero.");
        }
    }

    //Salva um novo quarto no sistema após validação.
    public void save(Room room) {
        validateRoom(room);
        roomDAO.save(room);
    }

    //Atualiza os dados de um quarto existente.
    public void update(Room room) {
        validateRoom(room);
        roomDAO.update(room);
    }

    //Remove um quarto do sistema.
    public void delete(Long id) {
        roomDAO.delete(id);
    }

    //Busca um quarto pelo ID.
    public Optional<Room> findById(Long id) {
        return roomDAO.findById(id);
    }

    //Busca um quarto pelo número.
    public Optional<Room> findByNumber(String roomNumber) {
        return roomDAO.findByNumber(roomNumber);
    }

    //Retorna todos os quartos cadastrados.
    public List<Room> findAll() {
        return roomDAO.findAll();
    }

    //Retorna apenas os quartos disponíveis para reserva.
    public List<Room> findAvailableRooms() {
        return roomDAO.findByStatus(RoomStatus.AVAILABLE);
    }

    //Retorna quartos filtrados por tipo (Single, Double, Luxury).
    public List<Room> findByType(RoomType type) {
        return roomDAO.findByType(type);
    }

    /**
     * Busca quartos com filtros combinados
     */
    public List<Room> findWithFilters(RoomType type, RoomStatus status) {
        List<Room> rooms = findAll();

        if (type != null) {
            rooms = rooms.stream()
                    .filter(room -> room.getType() == type)
                    .toList();
        }

        if (status != null) {
            rooms = rooms.stream()
                    .filter(room -> room.getStatus() == status)
                    .toList();
        }

        return rooms;
    }
}