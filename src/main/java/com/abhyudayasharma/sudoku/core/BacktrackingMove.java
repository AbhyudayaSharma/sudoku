package com.abhyudayasharma.sudoku.core;

import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(makeFinal = true)
class BacktrackingMove extends AbstractMove {
    private final int fromRow;
    private final int fromCol;
    private final int toRow;
    private final int toCol;

    BacktrackingMove(int fromRow, int fromCol, int toRow, int toCol) {
        super(MoveType.BACKTRACK);
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
    }
}
