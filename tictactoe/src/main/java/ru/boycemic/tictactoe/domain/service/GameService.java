package ru.boycemic.tictactoe.domain.service;


import ru.boycemic.tictactoe.domain.model.Game;

import java.util.UUID;


public interface GameService {


    Game makeMove(
            Game game,
            int row,
            int column,
            UUID currentUserId
    );


    boolean validateBoard(Game game);


    boolean isGameFinished(Game game);

}
