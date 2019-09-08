package com.abhyudayasharma.sudoku.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
class AssignmentMove extends AbstractMove {
    @Getter
    private final int row;
    @Getter
    private final int col;
    @Getter
    private final int oldValue;
    @Getter
    private final int newValue;

    AssignmentMove(int row, int col, int oldValue, int newValue) {
        super(MoveType.ASSIGNMENT);
        this.row = row;
        this.col = col;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}
