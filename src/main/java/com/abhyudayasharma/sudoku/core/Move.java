package com.abhyudayasharma.sudoku.core;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
class Move {
    @Getter
    private final int row;
    @Getter
    private final int col;
    @Getter
    private final int oldValue;
    @Getter
    private final int newValue;
}
