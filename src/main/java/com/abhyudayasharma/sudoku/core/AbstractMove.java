package com.abhyudayasharma.sudoku.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
abstract class AbstractMove {
    @Getter
    final MoveType moveType;
}
