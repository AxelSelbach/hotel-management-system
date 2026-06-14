package br.com.hotel.service;

import br.com.hotel.dao.ReservationDAO;
import br.com.hotel.dao.impl.ReservationDAOImpl;
import br.com.hotel.model.Reservation;
import br.com.hotel.model.RoomStatus;


import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * Service responsável pelas regras de negócio de reservas.
 * Contém lógica de cálculo de diárias, validações e operações de check-in/check-out.
 */
public class ReservationService {

    private final ReservationDAO reservationDAO;
    private final RoomService roomService;

    public ReservationService() {
        this.reservationDAO = new ReservationDAOImpl();
        this.roomService = new RoomService();
    }

    //Calcula o valor total da reserva com base na quantidade de diárias
    private void calculateTotalAmount(Reservation reservation) {
        long days = ChronoUnit.DAYS.between(reservation.getCheckInDate(), reservation.getCheckOutDate());
        if (days <= 0) {
            throw new RuntimeException("A data de check-out deve ser posterior à data de check-in.");
        }
        double total = days * reservation.getRoom().getDailyRate();
        reservation.setTotalAmount(total);
    }

    //Valida os dados obrigatórios de uma reserva
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

    //Realiza o check-in de uma reserva, alterando o status do quarto para ocupado.
    public void checkIn(Long reservationId) {
        Optional<Reservation> optReservation = reservationDAO.findById(reservationId);
        if (optReservation.isEmpty()) {
            throw new RuntimeException("Reserva não encontrada.");
        }

        Reservation reservation = optReservation.get();
        if (reservation.getRoom().getStatus() != RoomStatus.AVAILABLE) {
            throw new RuntimeException("Quarto não está disponível para check-in.");
        }

        reservationDAO.checkIn(reservationId);
    }

    //Realiza o check-out de uma reserva, liberando o quarto.
    public double checkOut(Long reservationId) {
        Optional<Reservation> optReservation = reservationDAO.findById(reservationId);
        if (optReservation.isEmpty()) {
            throw new RuntimeException("Reserva não encontrada.");
        }

        reservationDAO.checkOut(reservationId);
        return optReservation.get().getTotalAmount();
    }

    //Salva uma nova reserva, validando dados e calculando o valor total.
    public void save(Reservation reservation) {
        validateReservation(reservation);
        calculateTotalAmount(reservation);
        reservationDAO.save(reservation);
    }

    //Retorna todas as reservas
    public List<Reservation> findAll() {
        return reservationDAO.findAll();
    }

    //Retorna todas as reservas pelo ID do hóspede
    public List<Reservation> findByGuestId(Long guestId) {
        return reservationDAO.findByGuestId(guestId);
    }
}