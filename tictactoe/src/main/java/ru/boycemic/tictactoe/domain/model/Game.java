package ru.boycemic.tictactoe.domain.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Convert(converter = BoardConverter.class)
    @Column(name = "board", columnDefinition = "text")
    private int[][] board;

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player1_id")
    private User player1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player2_id")
    private User player2;

    @Column(name = "player1_mark")
    private int player1Mark = 1;

    @Column(name = "player2_mark")
    private int player2Mark = 2;

    /** Игра против компьютера: player2 всегда остаётся null, ходы бота делает GameServiceImpl. */
    @Column(name = "vs_computer", nullable = false)
    private boolean vsComputer;

    /** UUID игрока, чей сейчас ход; null, если игра ещё не началась или уже завершена. */
    @Column(name = "current_turn_player_id")
    private UUID currentTurnPlayerId;

    /** UUID победителя; null для ничьи, для незавершённой игры, а также при победе компьютера. */
    @Column(name = "winner_id")
    private UUID winnerId;

    public Game() {
    }

    public Game(UUID id, int[][] board, GameStatus status, User player1, User player2, boolean vsComputer) {
        this.id = id;
        this.board = board;
        this.status = status;
        this.player1 = player1;
        this.player2 = player2;
        this.vsComputer = vsComputer;
    }

    public UUID getId() {
        return id;
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public User getPlayer1() {
        return player1;
    }

    public User getPlayer2() {
        return player2;
    }

    public void setPlayer2(User player2) {
        this.player2 = player2;
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

    public void setCurrentTurnPlayerId(UUID currentTurnPlayerId) {
        this.currentTurnPlayerId = currentTurnPlayerId;
    }

    public UUID getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(UUID winnerId) {
        this.winnerId = winnerId;
    }
}
