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

package com.abhyudayasharma.sudoku.core;

import com.abhyudayasharma.sudoku.SudokuBoard;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.SwingWorker;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.abhyudayasharma.sudoku.SudokuBoard.SIZE;

/**
 * <i>Artificial Intelligence</i> for solving a sudoku puzzle.
 *
 * @author Abhyudaya Sharma
 */
@Slf4j
public abstract class SudokuSolver extends SwingWorker<Result, AbstractMove> {
    private static final int sqrt = (int) Math.rint(Math.sqrt(SIZE));

    private final int delay;

    protected SudokuSolver(SudokuBoard board, int delay) {
        initialBoard = board;
        matrix = initialBoard.asMatrix();
        if (delay < 0) {
            throw new IllegalArgumentException("Delay should be greater than 0");
        }
        this.delay = delay;
    }

    private final SudokuBoard initialBoard;
    private final int[][] matrix;
    private final Stack<AssignmentMove> moves = new Stack<>();

    /**
     * Set of values contained by each row.
     */
    private final List<Set<Integer>> rowValues = new ArrayList<>(SIZE);
    /**
     * Set of values contained by each column.
     */
    private final List<Set<Integer>> colValues = new ArrayList<>(SIZE);
    /**
     * Set of values contained by the small squares inside the board.
     * The top left has index 0. The bottom right has index {@link SudokuBoard#SIZE}&nbsp;{@code - 1}.
     */
    private final List<Set<Integer>> boxValues = new ArrayList<>(SIZE);
    private boolean isSolved = false;
    private int backtrackCount = 0;

    @Override
    protected abstract void done();

    @Override
    protected abstract void process(List<AbstractMove> chunks);

    /**
     * Solves a {@link SudokuBoard} and returns a new fully solved one.
     */
    private Result solve() throws ExecutionException, InterruptedException {
        if (isSolved) {
            return get();
        }

        isSolved = true;

        if (!initialBoard.isValid()) {
            throw new IllegalArgumentException("The sudoku board is not valid.");
        }

        initSets();
        try {
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix.length; j++) {
                    if (initialBoard.get(i, j).isPresent()) {
                        continue;
                    }

                    boolean wasAdded = false;
                    val oldValue = matrix[i][j];
                    val boxIndex = getBoxIndex(i, j);

                    for (var cellValue = oldValue + 1; cellValue <= SIZE; cellValue++) {
                        if (!(rowValues.get(i).contains(cellValue) || colValues.get(j).contains(cellValue) ||
                                  boxValues.get(boxIndex).contains(cellValue))) {
                            matrix[i][j] = cellValue;
                            rowValues.get(i).add(cellValue);
                            colValues.get(j).add(cellValue);
                            boxValues.get(boxIndex).add(cellValue);
                            wasAdded = true;
                            val move = new AssignmentMove(i, j, oldValue, cellValue);
                            moves.push(move);
                            publish(move);
                            // make it slower
                            Thread.sleep(delay);
                            break;
                        }
                    }

                    // go to the previous cell and try to increment the value
                    if (!wasAdded) {
                        matrix[i][j] = 0;
                        val previousCell = backtrack();
                        var row = previousCell.getLeft();
                        var col = previousCell.getRight();
                        val value = matrix[row][col];
                        publish(new BacktrackingMove(i, j, row, col));

                        // remove values because the incremented value would be set up in the next iteration
                        rowValues.get(row).remove(value);
                        colValues.get(col).remove(value);
                        boxValues.get(getBoxIndex(row, col)).remove(value);

                        // set up i and j to become equal to row and col for the next iteration
                        i = row;
                        j = col - 1;
                    }
                }
            }
        } catch (EmptyStackException e) {
            // moves stack becomes empty when there is no possible value to be put in the puzzle
            // and throws the EmptyStackException. This means that the puzzle is invalid.
            // for example, consider the puzzle
            //      1 2 3 4 5 6 7 8 X
            //      X X X X X X X X 2
            //      X X X X X X X X 3
            //      X X X X X X X X 4
            //      X X X X X X X X 5
            //      X X X X X X X X 6
            //      X X X X X X X X 7
            //      X X X X X X X X 8
            //      X X X X X X X X 9
            // taken from https://boards.straightdope.com/sdmb/archive/index.php/t-458783.html which
            // is valid but not a correct sudoku puzzle.
            throw new IllegalArgumentException("The entered pattern is not a valid sudoku puzzle.", e);
        }

        val resultBoard = new SudokuBoard(
            Arrays.stream(matrix).map(row -> Arrays.stream(row).mapToObj(String::valueOf).collect(Collectors.toUnmodifiableList()))
                .collect(Collectors.toUnmodifiableList()));

        return new Result(resultBoard, backtrackCount);
    }

    /**
     * Find the cell least recently visited cell whose value can still be increased.
     * In the process, reset the cells that were visited.
     *
     * @return which row and column to iterate from in the next loop, respectively
     */
    private Pair<Integer, Integer> backtrack() {
        backtrackCount++;
        var move = moves.pop();

        while (matrix[move.getRow()][move.getCol()] > SIZE) {
            val row = move.getRow();
            val col = move.getCol();
            val value = matrix[row][col];

            rowValues.get(row).remove(value);
            colValues.get(col).remove(value);
            boxValues.get(getBoxIndex(row, col)).remove(value);
            matrix[row][col] = 0;
            publish(new AssignmentMove(row, col, value, 0));

            move = moves.pop();
        }

        return Pair.of(move.getRow(), move.getCol());
    }

    private int getBoxIndex(int row, int col) {
        val boxRow = row / sqrt;
        val boxCol = col / sqrt;
        return boxRow * sqrt + boxCol;
    }

    private void initSets() {
        for (int i = 0; i < SIZE; i++) {
            rowValues.add(new HashSet<>());
            colValues.add(new HashSet<>());
            boxValues.add(new HashSet<>());
        }

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                val row = i;
                val col = j;
                initialBoard.get(i, j).ifPresent(x -> {
                    rowValues.get(row).add(x);
                    colValues.get(col).add(x);
                    boxValues.get(getBoxIndex(row, col)).add(x);
                });
            }
        }
    }

    @Override
    protected Result doInBackground() throws Exception {
        return solve();
    }
}
