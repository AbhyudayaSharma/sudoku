package com.abhyudayasharma.sudoku.ui;

import com.abhyudayasharma.sudoku.Sudoku;
import com.abhyudayasharma.sudoku.SudokuBoard;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.net.URI;

public class SudokuTable extends JTable {
    private static final int CELL_SIZE = 60;

    private static final DefaultTableCellRenderer defaultRenderer = new SudokuTableCellRenderer();

    @Override
    public SudokuTableModel getModel() {
        return (SudokuTableModel) super.getModel();
    }

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        return new SudokuCellEditor();
    }

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
        setFont(Sudoku.BOARD_FONT);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, false));
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        return defaultRenderer;
    }

    public SudokuBoard getBoard() {
        return getModel().asBoard();
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

    public void clear() {
        getModel().clear();
        repaint();
    }
}
