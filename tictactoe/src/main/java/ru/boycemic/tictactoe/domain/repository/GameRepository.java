package ru.boycemic.tictactoe.domain.repository;

import org.springframework.data.repository.CrudRepository;
import ru.boycemic.tictactoe.domain.model.Game;

import java.util.UUID;

public interface GameRepository extends CrudRepository<Game, UUID> {
}
