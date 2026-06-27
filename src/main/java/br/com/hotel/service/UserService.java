package br.com.hotel.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.hotel.dao.UserDAO;
import br.com.hotel.dao.impl.UserDAOImpl;
import br.com.hotel.model.User;
import java.util.Optional;

public class UserService {

    private final UserDAO userDAO = new UserDAOImpl();

    public User authenticate(String username, String password) {
        System.out.println("Tentativa de login - Usuário: " + username);

        Optional<User> userOpt = userDAO.findByUsername(username);
        if (userOpt.isEmpty()) {
            System.out.println("Usuário não encontrado: " + username);
            return null;
        }

        User user = userOpt.get();
        System.out.println("Usuário encontrado.");

        // Comparação com BCrypt
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());

        if (result.verified) {
            System.out.println("Login bem-sucedido!");
            return user;
        } else {
            System.out.println("Senha incorreta.");
            return null;
        }
    }

    public void createUser(String username, String password, String name, String role) {
        String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        User user = new User();
        user.setUsername(username);
        user.setPassword(hashedPassword);
        user.setName(name);
        user.setRole(role);
        userDAO.save(user);
    }
}