package br.com.hotel.service;

import br.com.hotel.dao.ReservationDAO;
import br.com.hotel.dao.impl.ReservationDAOImpl;
import br.com.hotel.model.Reservation;
import br.com.hotel.model.ReservationStatus;
import br.com.hotel.model.Room;
import br.com.hotel.model.RoomStatus;
import br.com.hotel.service.RoomService;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * Service responsável pelas regras de negócio de reservas.
 */
public class ReservationService {

    private final ReservationDAO reservationDAO;
    private final RoomService roomService;

    public ReservationService() {
        this.reservationDAO = new ReservationDAOImpl();
        this.roomService = new RoomService();
    }

    private void calculateTotalAmount(Reservation reservation) {
        long days = ChronoUnit.DAYS.between(reservation.getCheckInDate(), reservation.getCheckOutDate());
        if (days <= 0) {
            throw new RuntimeException("A data de check-out deve ser posterior à data de check-in.");
        }
        double total = days * reservation.getRoom().getDailyRate();
        reservation.setTotalAmount(total);
    }

    private void validateReservation(Reservation reservation) {
        if (reservation.getGuest() == null || reservation.getGuest().getId() == null) {
            throw new RuntimeException("Hóspede é obrigatório.");
        }
        if (reservation.getRoom() == null || reservation.getRoom().getId() == null) {
            throw new RuntimeException("Quarto é obrigatório.");
        }
        if (reservation.getCheckInDate() == null || reservation.getCheckOutDate() == null) {
            throw new RuntimeException("Datas de check-in e check-out são obrigatórias.");
        }
    }

    public void save(Reservation reservation) {
        validateReservation(reservation);
        calculateTotalAmount(reservation);

        Room room = reservation.getRoom();
        if (room.getStatus() != RoomStatus.AVAILABLE) {
            throw new RuntimeException("Quarto não está disponível para reserva.");
        }

        room.setStatus(RoomStatus.OCCUPIED);
        roomService.update(room);

        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationDAO.save(reservation);
    }

    public void checkIn(Long reservationId) {
        Optional<Reservation> opt = reservationDAO.findById(reservationId);
        if (opt.isEmpty()) {
            throw new RuntimeException("Reserva não encontrada.");
        }

        Reservation reservation = opt.get();

        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new RuntimeException("Só é possível fazer check-in em reservas confirmadas.");
        }

        reservationDAO.checkIn(reservationId);

        reservation.setStatus(ReservationStatus.CHECKED_IN);
        reservationDAO.update(reservation);
    }

    public double checkOut(Long reservationId) {
        Optional<Reservation> optReservation = reservationDAO.findById(reservationId);
        if (optReservation.isEmpty()) {
            throw new RuntimeException("Reserva não encontrada.");
        }

        Reservation reservation = optReservation.get();

        if (reservation.getStatus() == ReservationStatus.CHECKED_OUT) {
            throw new RuntimeException("Esta reserva já foi finalizada.");
        }

        reservationDAO.checkOut(reservationId);

        reservation.setStatus(ReservationStatus.CHECKED_OUT);
        reservationDAO.update(reservation);

        Room room = reservation.getRoom();
        room.setStatus(RoomStatus.AVAILABLE);
        roomService.update(room);

        return reservation.getTotalAmount();
    }

    public void update(Reservation reservation) {
        reservationDAO.update(reservation);
    }

    public List<Reservation> findAll() {
        return reservationDAO.findAll();
    }

    public List<Reservation> findByGuestId(Long guestId) {
        return reservationDAO.findByGuestId(guestId);
    }
}