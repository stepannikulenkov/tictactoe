package ru.boycemic.tictactoe.domain.service;

import ru.boycemic.tictactoe.domain.model.User;
import ru.boycemic.tictactoe.web.model.SignUpRequest;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    boolean register(SignUpRequest request);

    Optional<User> findByLogin(String login);

    User authenticate(String login, String password);

    Optional<User> findById(UUID id);
}
