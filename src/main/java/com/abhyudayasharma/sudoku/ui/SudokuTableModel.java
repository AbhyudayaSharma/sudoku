package com.abhyudayasharma.sudoku.ui;

import com.abhyudayasharma.sudoku.SudokuBoard;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SudokuTableModel implements TableModel {
    private final List<List<String>> data;
    private final List<TableModelListener> listeners = new ArrayList<>();

    SudokuTableModel() {
        data = new ArrayList<>();
        for (int i = 0; i < SudokuBoard.SIZE; i++) {
            var row = new ArrayList<>(Collections.nCopies(SudokuBoard.SIZE, ""));
            data.add(row);
        }
    }

    SudokuTableModel(URI uri) throws Exception {
        SudokuBoard board = SudokuBoard.load(uri);
        data = board.asList();
    }

    @Override
    public int getRowCount() {
        return SudokuBoard.SIZE;
    }

    @Override
    public int getColumnCount() {
        return SudokuBoard.SIZE;
    }

    @Override
    public String getColumnName(int i) {
        // columns have no name
        return "";
    }

    @Override
    public Class<?> getColumnClass(int i) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int i, int i1) {
        return true;
    }

    @Override
    public Object getValueAt(int row, int col) {
        return data.get(row).get(col);
    }

    @Override
    public void setValueAt(Object o, int row, int col) {
        if (!(o instanceof String)) {
            throw new IllegalArgumentException("The object passed should be a String");
        }
        data.get(row).set(col, (String) o);
        listeners.forEach(listener -> listener.tableChanged(new TableModelEvent(this, row)));
    }

    @Override
    public void addTableModelListener(TableModelListener tableModelListener) {
        listeners.add(tableModelListener);
    }

    @Override
    public void removeTableModelListener(TableModelListener tableModelListener) {
        listeners.remove(tableModelListener);
    }
}
