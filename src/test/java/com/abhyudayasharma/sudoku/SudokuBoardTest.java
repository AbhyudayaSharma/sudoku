/*
 * MIT License
 *
 * Copyright (c) 2019 Abhyudaya Sharma
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
