package ru.boycemic.tictactoe.web.controller;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.boycemic.tictactoe.web.exception.*;


@RestControllerAdvice
public class ExceptionHandlerController {


    @ExceptionHandler({
            GameNotFoundException.class,
            UserNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(
            RuntimeException e
    ){
        return e.getMessage();
    }



    @ExceptionHandler({
            InvalidMoveException.class,
            GameFinishedException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(
            RuntimeException e
    ){
        return e.getMessage();
    }



    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleUnauthorized(
            InvalidCredentialsException e
    ){
        return e.getMessage();
    }



    @ExceptionHandler(GameNotAvailableException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleConflict(
            GameNotAvailableException e
    ){
        return e.getMessage();
    }
}
