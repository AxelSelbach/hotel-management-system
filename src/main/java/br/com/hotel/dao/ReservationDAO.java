package br.com.hotel.dao;

import br.com.hotel.model.Reservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationDAO {
    void save(Reservation reservation);
    void update(Reservation reservation);
    void delete(Long id);

    Optional<Reservation> findById(Long id);
    List<Reservation> findAll();
    List<Reservation> findByGuestId(Long guestId);
    List<Reservation> findByRoomId(Long roomId);
    List<Reservation> findByCheckInDateRange(LocalDate start, LocalDate end);

    //Business rules (Regras de negócios check in e check out)
    boolean checkIn(Long reservationId);
    boolean checkOut(Long reservationId);
}
