package br.com.hotel.dao;

import br.com.hotel.model.User;
import java.util.Optional;

public interface UserDAO {
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameAndPassword(String username, String password);
    void save(User user);
}