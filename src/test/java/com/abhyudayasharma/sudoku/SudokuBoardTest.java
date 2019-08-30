package com.abhyudayasharma.sudoku;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SudokuBoardTest {
    @Test
    void sizeTest() {
        assertEquals(9, SudokuBoard.SIZE);
        var sqrt = Math.sqrt(SudokuBoard.SIZE);

        // should be a perfect square
        assertTrue(sqrt == Math.floor(sqrt) && !Double.isInfinite(sqrt));
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
