package com.abhyudayasharma.sudoku.core;

import com.abhyudayasharma.sudoku.SudokuBoard;
import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
public class Result {
    @NonNull
    final SudokuBoard board;
    final int backTrackCount;
    @NonNull
    final List<AbstractMove> moves;
}
