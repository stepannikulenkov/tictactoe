package ru.boycemic.tictactoe.domain.model;

public enum GameStatus {

    /** Игра создана, ожидает второго игрока (для игры с компьютером не используется). */
    WAITING_FOR_PLAYERS,
    /** Игра идёт, чей сейчас ход — см. {@link Game#getCurrentTurnPlayerId()}. */
    IN_PROGRESS,
    /** Ничья. */
    DRAW,
    /** Игра завершена победой — победитель см. {@link Game#getWinnerId()} (null, если победил компьютер). */
    FINISHED

}
