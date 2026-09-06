package ru.boycemic.tictactoe.web.mapper;

import org.springframework.stereotype.Component;
import ru.boycemic.tictactoe.domain.model.Game;
import ru.boycemic.tictactoe.web.model.GameResponse;


@Component
public class GameWebMapper {


    public GameResponse toResponse(Game game) {

        return new GameResponse(
                game.getId(),
                game.getBoard(),
                game.getStatus(),
                game.getPlayer1() != null ? game.getPlayer1().getId() : null,
                game.getPlayer2() != null ? game.getPlayer2().getId() : null,
                game.getPlayer1Mark(),
                game.getPlayer2Mark(),
                game.isVsComputer(),
                game.getCurrentTurnPlayerId(),
                game.getWinnerId()
        );
    }
}
