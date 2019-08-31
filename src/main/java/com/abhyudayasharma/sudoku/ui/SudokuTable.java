package com.abhyudayasharma.sudoku.ui;

import com.abhyudayasharma.sudoku.Sudoku;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.net.URI;

public class SudokuTable extends JTable {
    private static final int CELL_SIZE = 60;

    public SudokuTable() {
        super(new SudokuTableModel());
        cellSelectionEnabled = rowSelectionAllowed = false;
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        showVerticalLines = true;
        showHorizontalLines = true;
        autoResizeMode = JTable.AUTO_RESIZE_OFF;
        rowHeight = CELL_SIZE;
        tableHeader = null;

        setColumnCellEditors();
        var centeredRenderer = new DefaultTableCellRenderer();
        centeredRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        setDefaultRenderer(Object.class, centeredRenderer);
        setFont(Sudoku.BOARD_FONT);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, false));
    }

    private void setColumnCellEditors() {
        for (var it = columnModel.getColumns().asIterator(); it.hasNext(); ) {
            var column = it.next();
            column.setPreferredWidth(CELL_SIZE);
            column.setCellEditor(new SudokuCellEditor());
        }
    }

    /**
     * Load the table from a CSV file
     *
     * @param uri uri to a CSV fil
     * @throws Exception if unable to load the file
     */
    public void load(URI uri) throws Exception {
        setModel(new SudokuTableModel(uri));
        setColumnCellEditors();
    }
}
