package com.abhyudayasharma.sudoku.ui;

import com.abhyudayasharma.sudoku.Sudoku;
import com.abhyudayasharma.sudoku.SudokuBoard;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.net.URI;

public class SudokuTable extends JTable {
    private static final int CELL_SIZE = 60;

    @Override
    public SudokuTableModel getModel() {
        return (SudokuTableModel) super.getModel();
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
        var centeredRenderer = new DefaultTableCellRenderer() {
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
        };
        centeredRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        centeredRenderer.setBorder(BorderFactory.createLineBorder(Color.BLUE, 10));

        setDefaultRenderer(Object.class, centeredRenderer);
        setFont(Sudoku.BOARD_FONT);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, false));
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
