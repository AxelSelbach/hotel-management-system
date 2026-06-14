package br.com.hotel.dao;

import br.com.hotel.model.Guest;

import java.util.List;
import java.util.Optional;

public interface GuestDAO {
    void save(Guest guest);
    void update(Guest guest);
    void delete(Long id);

    Optional<Guest> findById(Long id);
    Optional<Guest> findByCpf(String cpf);
    List<Guest> findAll();
    List<Guest> findByName(String name);
}
