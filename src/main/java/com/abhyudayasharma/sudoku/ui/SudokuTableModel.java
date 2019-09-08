package com.abhyudayasharma.sudoku.ui;

import com.abhyudayasharma.sudoku.SudokuBoard;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FilenameUtils;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class SudokuTableModel implements TableModel {
    private final List<List<String>> data;
    private final List<TableModelListener> listeners = new Vector<>();

    SudokuTableModel() {
        data = new ArrayList<>();
        for (int i = 0; i < SudokuBoard.SIZE; i++) {
            var row = new ArrayList<>(Collections.nCopies(SudokuBoard.SIZE, ""));
            data.add(row);
        }
    }

    public SudokuTableModel(SudokuBoard board) {
        data = board.asList();
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
    public synchronized Object getValueAt(int row, int col) {
        return data.get(row).get(col);
    }

    @Override
    public synchronized void setValueAt(Object o, int row, int col) {
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

    synchronized SudokuBoard asBoard() {
        return new SudokuBoard(data);
    }

    /**
     * Save the current table model as a CSV
     *
     * @param file the file to be written to
     * @throws IOException when unable to write the file
     */
    public void save(File file) throws IOException {
        if (!FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("csv")) {
            file = new File(file.toString() + ".csv");
        }
        try (CSVPrinter printer = new CSVPrinter(new BufferedWriter(new FileWriter(
            file, StandardCharsets.UTF_8)), CSVFormat.RFC4180)) {
            printer.printRecords(data);
        }
    }

    /**
     * Clear all data from the model.
     */
    synchronized void clear() {
        data.forEach(List::clear);
        data.forEach(row -> row.addAll(Collections.nCopies(SudokuBoard.SIZE, "")));
    }
}
