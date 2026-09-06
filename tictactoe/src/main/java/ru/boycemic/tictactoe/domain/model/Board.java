package ru.boycemic.tictactoe.domain.model;

public class Board {

    private int[][] cells;


    public Board() {
        this.cells = new int[3][3];
    }


    public Board(int[][] cells) {
        this.cells = cells;
    }


    public int[][] getCells() {
        return cells;
    }


    public void setCells(int[][] cells) {
        this.cells = cells;
    }


    public boolean isEmpty(int row, int column) {

        return cells[row][column] == 0;
    }


    public void setCell(int row, int column, int value) {

        cells[row][column] = value;
    }
    public boolean hasWinner(int player) {

        // строки
        for (int i = 0; i < 3; i++) {

            if (cells[i][0] == player &&
                    cells[i][1] == player &&
                    cells[i][2] == player) {

                return true;
            }
        }


        // столбцы
        for (int i = 0; i < 3; i++) {

            if (cells[0][i] == player &&
                    cells[1][i] == player &&
                    cells[2][i] == player) {

                return true;
            }
        }


        // диагонали

        if (cells[0][0] == player &&
                cells[1][1] == player &&
                cells[2][2] == player) {

            return true;
        }


        if (cells[0][2] == player &&
                cells[1][1] == player &&
                cells[2][0] == player) {

            return true;
        }


        return false;
    }



    public boolean isFull() {

        for (int i = 0; i < 3; i++) {

            for (int j = 0; j < 3; j++) {

                if (cells[i][j] == 0) {
                    return false;
                }
            }
        }

        return true;
    }
}