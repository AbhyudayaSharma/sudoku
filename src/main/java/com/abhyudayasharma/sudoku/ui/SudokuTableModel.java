/*
 * MIT License
 *
 * Copyright (c) 2019 Abhyudaya Sharma
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.abhyudayasharma.sudoku.ui;

import com.abhyudayasharma.sudoku.SudokuBoard;
import lombok.Getter;
import lombok.Setter;
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

    @Getter
    @Setter
    private boolean isEditable = true;

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
        return isEditable;
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
