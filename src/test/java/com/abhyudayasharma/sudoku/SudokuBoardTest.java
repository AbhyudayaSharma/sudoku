package com.abhyudayasharma.sudoku;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SudokuBoardTest {
    @Test
    void sizeTest() {
        Assertions.assertEquals(9, SudokuBoard.SIZE);
    }

    @Test
    void csvLoadTest1() throws Exception {
        final int[][] expected = new int[][]{
            {1, 0, 3, 4, 5, 6, 7, 8, 9},
            {1, 0, 3, 4, 5, 6, 7, 8, 9},
            {1, 0, 3, 4, 5, 6, 7, 8, 9},
            {1, 0, 3, 4, 5, 6, 7, 8, 9},
            {1, 0, 3, 4, 5, 6, 7, 8, 9},
            {1, 0, 3, 4, 5, 6, 7, 8, 9},
            {1, 0, 3, 4, 5, 6, 7, 8, 9},
            {1, 0, 3, 4, 5, 6, 7, 8, 9},
            {1, 0, 3, 4, 5, 6, 7, 8, 9},
        };

        SudokuBoard board = SudokuBoard.load(getClass().getResource("sudoku1.csv").toURI());
        for (int i = 0; i < SudokuBoard.SIZE; i++) {
            for (int j = 0; j < SudokuBoard.SIZE; j++) {
                assertEquals(expected[i][j], board.get(i, j).orElse(0));
            }
        }
    }
}
