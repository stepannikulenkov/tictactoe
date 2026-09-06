package ru.boycemic.tictactoe.datasource.repository;

import org.springframework.data.repository.CrudRepository;
import ru.boycemic.tictactoe.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {

    Optional<User> findByLogin(String login);
}
