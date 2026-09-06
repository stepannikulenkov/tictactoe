package ru.boycemic.tictactoe.domain.service;

import org.springframework.stereotype.Service;
import ru.boycemic.tictactoe.datasource.repository.UserRepository;
import ru.boycemic.tictactoe.domain.model.User;
import ru.boycemic.tictactoe.web.exception.InvalidCredentialsException;
import ru.boycemic.tictactoe.web.model.SignUpRequest;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean register(SignUpRequest request) {
        if (userRepository.findByLogin(request.getLogin()).isPresent()) {
            return false;
        }
        User user = new User(request.getLogin(), request.getPassword());
        userRepository.save(user);
        return true;
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    @Override
    public User authenticate(String login, String password) {
        return userRepository.findByLogin(login)
                .filter(user -> user.getPassword().equals(password))
                .orElseThrow(() -> new InvalidCredentialsException("Invalid login or password"));
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }
}
