package br.com.hotel.dao;

import br.com.hotel.model.Room;
import br.com.hotel.model.RoomStatus;
import br.com.hotel.model.RoomType;

import java.util.List;
import java.util.Optional;

public interface RoomDAO {
    void save(Room room);
    void update(Room room);
    void delete(Long id);

    Optional<Room> findById(Long id);
    Optional<Room> findByNumber(String roomNumber);
    List<Room> findAll();
    List<Room> findByStatus(RoomStatus status);
    List<Room> findByType(RoomType type);
}
