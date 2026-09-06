package ru.boycemic.tictactoe.web.model;

public class CreateGameRequest {

    private OpponentType opponent = OpponentType.HUMAN;

    public CreateGameRequest() {
    }

    public CreateGameRequest(OpponentType opponent) {
        this.opponent = opponent;
    }

    public OpponentType getOpponent() {
        return opponent;
    }

    public void setOpponent(OpponentType opponent) {
        this.opponent = opponent;
    }

    public enum OpponentType {
        HUMAN,
        COMPUTER
    }
}
