package ru.boycemic.tictactoe.domain.service;


import ru.boycemic.tictactoe.domain.model.Board;
import ru.boycemic.tictactoe.domain.model.Game;
import ru.boycemic.tictactoe.domain.model.GameStatus;
import ru.boycemic.tictactoe.domain.repository.GameRepository;
import ru.boycemic.tictactoe.web.exception.GameFinishedException;
import ru.boycemic.tictactoe.web.exception.InvalidMoveException;

import java.util.UUID;


public class GameServiceImpl implements GameService {


    private final GameRepository gameRepository;


    public GameServiceImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }


    @Override
    public Game makeMove(
            Game game,
            int row,
            int column,
            UUID currentUserId
    ) {

        if (game.getStatus() == GameStatus.WAITING_FOR_PLAYERS) {
            throw new InvalidMoveException("Waiting for the second player to join");
        }

        if (isGameFinished(game)) {
            throw new GameFinishedException("Game is already finished");
        }

        if (!currentUserId.equals(game.getCurrentTurnPlayerId())) {
            throw new InvalidMoveException("It is not your turn");
        }

        if (row < 0 || row > 2 || column < 0 || column > 2) {
            throw new InvalidMoveException("Invalid coordinates");
        }

        boolean player1Turn = currentUserId.equals(game.getPlayer1().getId());
        int currentMark = player1Turn ? game.getPlayer1Mark() : game.getPlayer2Mark();

        Board board = new Board(game.getBoard());

        if (!board.isEmpty(row, column)) {
            throw new InvalidMoveException("Cell already occupied");
        }

        board.setCell(row, column, currentMark);
        game.setBoard(board.getCells());

        // проверяем победу текущего игрока

        if (board.hasWinner(currentMark)) {
            finish(game, currentUserId);
            return gameRepository.save(game);
        }

        // проверяем ничью после хода

        if (board.isFull()) {
            finishDraw(game);
            return gameRepository.save(game);
        }

        // если игра с компьютером, ход компьютера сразу же после хода игрока

        if (game.isVsComputer()) {
            makeComputerMove(game, board);
            return gameRepository.save(game);
        }

        // иначе ход переходит второму игроку

        UUID nextPlayerId = player1Turn ? game.getPlayer2().getId() : game.getPlayer1().getId();
        game.setCurrentTurnPlayerId(nextPlayerId);

        return gameRepository.save(game);
    }


    private void makeComputerMove(Game game, Board board) {

        int computerMark = game.getPlayer2Mark();
        Minimax minimax = new Minimax();
        int[] move = minimax.findBestMove(board);

        if (move[0] != -1) {
            board.setCell(move[0], move[1], computerMark);
            game.setBoard(board.getCells());
        }

        // проверяем победу компьютера

        if (board.hasWinner(computerMark)) {
            finish(game, null);
            return;
        }

        // проверяем ничью после хода компьютера

        if (board.isFull()) {
            finishDraw(game);
            return;
        }

        game.setCurrentTurnPlayerId(game.getPlayer1().getId());
    }


    private void finish(Game game, UUID winnerId) {
        game.setStatus(GameStatus.FINISHED);
        game.setWinnerId(winnerId);
        game.setCurrentTurnPlayerId(null);
    }


    private void finishDraw(Game game) {
        game.setStatus(GameStatus.DRAW);
        game.setCurrentTurnPlayerId(null);
    }


    @Override
    public boolean validateBoard(Game game) {

        if (game == null) {
            return false;
        }

        return game.getBoard() != null;
    }


    @Override
    public boolean isGameFinished(Game game) {

        GameStatus status = game.getStatus();

        return status == GameStatus.DRAW || status == GameStatus.FINISHED;
    }
}
