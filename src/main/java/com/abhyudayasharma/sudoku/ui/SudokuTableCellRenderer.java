package com.abhyudayasharma.sudoku.ui;

import com.abhyudayasharma.sudoku.SudokuBoard;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;

public class SudokuTableCellRenderer extends DefaultTableCellRenderer {
    private final int sqrt = (int) Math.rint(Math.sqrt(SudokuBoard.SIZE));
    private final Color borderColor = Color.BLACK;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        final var component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        var border = BorderFactory.createEmptyBorder();

        final var borderThickness = 3;
        if (row % sqrt == sqrt - 1 && row != SudokuBoard.SIZE - 1) {
            border = BorderFactory.createCompoundBorder(border,
                BorderFactory.createMatteBorder(0, 0, borderThickness, 0, borderColor));
        }

        if (column % sqrt == sqrt - 1 && column != SudokuBoard.SIZE - 1) {
            border = BorderFactory.createCompoundBorder(border,
                BorderFactory.createMatteBorder(0, 0, 0, borderThickness, borderColor));
        }

        if (component instanceof JComponent) {
            ((JComponent) component).setBorder(border);
        } else {
            throw new IllegalArgumentException("component should be a JComponent");
        }

        return component;
    }

    @Override
    public int getHorizontalAlignment() {
        return SwingConstants.CENTER;
    }
}
