package com.abhyudayasharma.sudoku.core;

import com.abhyudayasharma.sudoku.SudokuBoard;
import lombok.Getter;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import static com.abhyudayasharma.sudoku.SudokuBoard.SIZE;

/**
 * <i>Artificial Intelligence</i> for solving a sudoku puzzle.
 *
 * @author Abhyudaya Sharma
 */
public class SudokuSolver {
    private static final int sqrt = (int) Math.rint(Math.sqrt(SIZE));

    private final SudokuBoard initialBoard;
    @Getter
    private final int[][] matrix;
    private final Stack<Move> moves = new Stack<>();
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

    public SudokuSolver(SudokuBoard board) {
        initialBoard = board;
        matrix = initialBoard.asMatrix();
    }

    /**
     * Solves a {@link SudokuBoard} and returns a new fully solved one.
     */
    public void solve() {
        if (isSolved) {
            return;
        }

        isSolved = true;

        if (!initialBoard.isValid()) {
            throw new IllegalArgumentException("The sudoku board is not valid.");
        }

        initSets();

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
                        moves.push(new Move(i, j, oldValue, cellValue));
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
    }

    /**
     * Find the cell least recently visited cell whose value can still be increased.
     * In the process, reset the cells that were visited.
     *
     * @return which row and column to iterate from in the next loop, respectively
     */
    private Pair<Integer, Integer> backtrack() {
        var move = moves.pop();

        while (matrix[move.getRow()][move.getCol()] > SIZE) {
            val row = move.getRow();
            val col = move.getCol();
            val value = matrix[row][col];

            rowValues.get(row).remove(value);
            colValues.get(col).remove(value);
            boxValues.get(getBoxIndex(row, col)).remove(value);
            matrix[row][col] = 0;

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
}