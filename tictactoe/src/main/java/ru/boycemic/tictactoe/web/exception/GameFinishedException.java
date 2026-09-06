package ru.boycemic.tictactoe.web.exception;

public class GameFinishedException extends RuntimeException {

    public GameFinishedException(String message) {
        super(message);
    }
}