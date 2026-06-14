package br.com.hotel.service;

import br.com.hotel.dao.GuestDAO;
import br.com.hotel.dao.impl.GuestDAOImpl;
import br.com.hotel.model.Guest;

import java.util.List;
import java.util.Optional;

/**
 * Service responsável pelas regras de negócio relacionadas a hóspedes.
 */
public class GuestService {
    private final GuestDAO guestDAO;

    public GuestService() {
        this.guestDAO = new GuestDAOImpl();
    }

    //Valida os campos obrigatórios de um hóspede
    private void validateGuest(Guest guest){
        if(guest.getName() == null || guest.getName().trim().isEmpty()){
            throw new RuntimeException("Nome do hóspede é obrigatório");
        }

        if(guest.getCpf() == null || guest.getCpf().trim().isEmpty()){
            throw new RuntimeException("CPF é obrigatório");
        }

        if(guest.getEmail() == null || guest.getEmail().trim().isEmpty()){
            throw new RuntimeException("Email é obrigatório");
        }
    }

    //Cadastra um novo hóspede após validações
    public void save(Guest guest){
        validateGuest(guest);
        //Validando se o CPF está cadastrado
        if(guestDAO.findByCpf(guest.getCpf()).isPresent()){
            throw new RuntimeException("Já existe um hóspede cadastrado com este CPF.");
        }
        guestDAO.save(guest);
    }

    //Atualizando hóspede
    public void update(Guest guest) {
        validateGuest(guest);
        guestDAO.update(guest);
    }

    //Deletando um hóspede
    public void delete(Long id) {
        guestDAO.delete(id);
    }

    //Buscando hóspede por ID
    public Optional<Guest> findById(Long id) {
        return guestDAO.findById(id);
    }

    //Buscando hóspede por CPF
    public Optional<Guest> findByCpf(String cpf) {
        return guestDAO.findByCpf(cpf);
    }

    //Listando todos os hóspedes
    public List<Guest> findAll() {
        return guestDAO.findAll();
    }

    //Listando hóspede pelo nome
    public List<Guest> findByName(String name) {
        return guestDAO.findByName(name);
    }
}
