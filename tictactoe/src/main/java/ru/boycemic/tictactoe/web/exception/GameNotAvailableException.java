package ru.boycemic.tictactoe.web.exception;

public class GameNotAvailableException extends RuntimeException {

    public GameNotAvailableException(String message) {
        super(message);
    }
}
