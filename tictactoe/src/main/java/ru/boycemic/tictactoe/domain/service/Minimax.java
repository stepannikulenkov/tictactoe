package ru.boycemic.tictactoe.domain.service;

import ru.boycemic.tictactoe.domain.model.Board;

public class Minimax {


    private static final int HUMAN = 1;
    private static final int COMPUTER = 2;


    public int[] findBestMove(Board board) {

        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = new int[]{-1, -1};


        int[][] cells = board.getCells();


        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {


                if (cells[i][j] == 0) {


                    cells[i][j] = COMPUTER;


                    int score = minimax(
                            cells,
                            false
                    );


                    cells[i][j] = 0;


                    if (score > bestScore) {

                        bestScore = score;

                        bestMove[0] = i;
                        bestMove[1] = j;
                    }
                }
            }
        }


        return bestMove;
    }



    private int minimax(
            int[][] board,
            boolean isMaximizing
    ) {


        Integer result = evaluate(board);


        if (result != null) {
            return result;
        }



        if (isMaximizing) {


            int bestScore = Integer.MIN_VALUE;


            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {


                    if (board[i][j] == 0) {


                        board[i][j] = COMPUTER;


                        int score = minimax(
                                board,
                                false
                        );


                        board[i][j] = 0;


                        bestScore = Math.max(
                                bestScore,
                                score
                        );
                    }
                }
            }


            return bestScore;


        } else {


            int bestScore = Integer.MAX_VALUE;


            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {


                    if (board[i][j] == 0) {


                        board[i][j] = HUMAN;


                        int score = minimax(
                                board,
                                true
                        );


                        board[i][j] = 0;


                        bestScore = Math.min(
                                bestScore,
                                score
                        );
                    }
                }
            }


            return bestScore;
        }
    }



    private Integer evaluate(int[][] board) {


        if (checkWin(board, COMPUTER)) {
            return 10;
        }


        if (checkWin(board, HUMAN)) {
            return -10;
        }


        if (isFull(board)) {
            return 0;
        }


        return null;
    }



    private boolean checkWin(
            int[][] b,
            int player
    ) {


        for (int i = 0; i < 3; i++) {


            if (b[i][0] == player &&
                    b[i][1] == player &&
                    b[i][2] == player) {

                return true;
            }


            if (b[0][i] == player &&
                    b[1][i] == player &&
                    b[2][i] == player) {

                return true;
            }
        }



        return
                (b[0][0] == player &&
                        b[1][1] == player &&
                        b[2][2] == player)

                        ||

                        (b[0][2] == player &&
                                b[1][1] == player &&
                                b[2][0] == player);
    }



    private boolean isFull(int[][] board) {


        for (int[] row : board) {

            for (int cell : row) {

                if (cell == 0) {
                    return false;
                }
            }
        }


        return true;
    }
}