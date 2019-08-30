package com.abhyudayasharma.sudoku.ui;

import com.abhyudayasharma.sudoku.Sudoku;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;

import static com.abhyudayasharma.sudoku.SudokuBoard.SIZE;

public class SudokuTable extends JTable {
    private static final int CELL_SIZE = 50;

    public SudokuTable() {
        super(SIZE, SIZE);
        cellSelectionEnabled = rowSelectionAllowed = false;
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        showVerticalLines = true;
        showHorizontalLines = true;
        autoResizeMode = JTable.AUTO_RESIZE_OFF;
        rowHeight = 50;
        tableHeader = null;

        for (var it = columnModel.getColumns().asIterator(); it.hasNext(); ) {
            var column = it.next();
            column.setPreferredWidth(CELL_SIZE);
            column.setCellEditor(new SudokuCellEditor());
        }

        var centeredRenderer = new DefaultTableCellRenderer();
        centeredRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        setDefaultRenderer(Object.class, centeredRenderer);
        setFont(Sudoku.BOARD_FONT);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, false));
    }
}
