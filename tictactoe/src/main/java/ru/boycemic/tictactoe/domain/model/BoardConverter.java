package ru.boycemic.tictactoe.domain.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Hibernate does not support mapping a raw 2D primitive array via
 * {@code @ElementCollection}, so the 3x3 board is serialized into a single
 * comma-separated text column (row-major order) and parsed back on read.
 */
@Converter
public class BoardConverter implements AttributeConverter<int[][], String> {

    private static final int SIZE = 3;

    @Override
    public String convertToDatabaseColumn(int[][] board) {
        if (board == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (int[] row : board) {
            for (int cell : row) {
                sb.append(cell).append(',');
            }
        }
        return sb.toString();
    }

    @Override
    public int[][] convertToEntityAttribute(String value) {
        int[][] board = new int[SIZE][SIZE];

        if (value == null || value.isBlank()) {
            return board;
        }

        String[] parts = value.split(",");
        for (int i = 0; i < parts.length && i < SIZE * SIZE; i++) {
            board[i / SIZE][i % SIZE] = Integer.parseInt(parts[i]);
        }
        return board;
    }
}
