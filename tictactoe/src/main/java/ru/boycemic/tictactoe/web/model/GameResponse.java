package ru.boycemic.tictactoe.web.model;

import ru.boycemic.tictactoe.domain.model.GameStatus;

import java.util.UUID;


public class GameResponse {

    private UUID id;
    private int[][] board;
    private GameStatus status;
    private UUID player1;
    private UUID player2;
    private int player1Mark;
    private int player2Mark;
    private boolean vsComputer;
    private UUID currentTurnPlayerId;
    private UUID winnerId;


    public GameResponse(
            UUID id,
            int[][] board,
            GameStatus status,
            UUID player1,
            UUID player2,
            int player1Mark,
            int player2Mark,
            boolean vsComputer,
            UUID currentTurnPlayerId,
            UUID winnerId
    ) {
        this.id = id;
        this.board = board;
        this.status = status;
        this.player1 = player1;
        this.player2 = player2;
        this.player1Mark = player1Mark;
        this.player2Mark = player2Mark;
        this.vsComputer = vsComputer;
        this.currentTurnPlayerId = currentTurnPlayerId;
        this.winnerId = winnerId;
    }


    public UUID getId() {
        return id;
    }


    public int[][] getBoard() {
        return board;
    }

    public GameStatus getStatus() {
        return status;
    }

    public UUID getPlayer1() {
        return player1;
    }

    public UUID getPlayer2() {
        return player2;
    }

    public int getPlayer1Mark() {
        return player1Mark;
    }

    public int getPlayer2Mark() {
        return player2Mark;
    }

    public boolean isVsComputer() {
        return vsComputer;
    }

    public UUID getCurrentTurnPlayerId() {
        return currentTurnPlayerId;
    }

    public UUID getWinnerId() {
        return winnerId;
    }
}
