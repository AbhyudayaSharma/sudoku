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

package com.abhyudayasharma.sudoku;

import com.abhyudayasharma.sudoku.core.AbstractMove;
import com.abhyudayasharma.sudoku.core.AssignmentMove;
import com.abhyudayasharma.sudoku.core.SudokuSolver;
import com.abhyudayasharma.sudoku.ui.SudokuTable;
import com.abhyudayasharma.sudoku.ui.SudokuTableModel;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CancellationException;

/**
 * A Sudoku puzzle emulation which can solve all valid Sudoku puzzles.
 *
 * @author Abhyudaya Sharma - 1710110019
 */
@Slf4j
public class Sudoku {
    /**
     * The {@link Font} that must be used for the sudoku board.
     */
    public static final Font BOARD_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 36);

    private final JFrame frame = new JFrame("Sudoku");
    private final SudokuTable table = new SudokuTable();
    private final JButton solveButton = new JButton("Solve");
    private final JButton clearButton = new JButton("Clear");
    private final JButton stopButton = new JButton("Stop");
    private final JLabel solvedLabel = new JLabel("Ready...");
    private SudokuSolver solver = null;

    void start() {
        initFrame();
    }

    /**
     * Initializes a {@link JFrame} for display.
     */
    private void initFrame() {
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new MigLayout());
        frame.add(table, "grow, wrap, span");
        frame.setJMenuBar(createMenuBar());

        var slider = new JSlider(0, 10);
        slider.setSize(slider.getPreferredSize());
        stopButton.setEnabled(false);

        solveButton.addActionListener(new TableActionListener() {
            @Override
            void actionPerformed() {
                var board = table.getBoard();
                stopButton.setEnabled(true);
                slider.setEnabled(false);
                solver = new SudokuSolver(board, 10 - slider.getValue()) {
                    @Override
                    protected void done() {
                        try {
                            final var result = get();
                            final var newModel = new SudokuTableModel(result.getBoard());
                            newModel.setEditable(false);
                            table.setModel(newModel);
                            solvedLabel.setText("Solved");
                        } catch (CancellationException e) {
                            clearButton.doClick();
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(frame, "Unable to solve: " + e.getCause().getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                            table.getModel().setEditable(true);
                            solveButton.setEnabled(true);
                            slider.setEnabled(true);
                            solvedLabel.setText("Ready...");
                        } finally {
                            clearButton.setEnabled(true);
                            stopButton.setEnabled(false);
                        }
                    }

                    @Override
                    protected void process(List<AbstractMove> moves) {
                        for (var move : moves) {
                            if (move instanceof AssignmentMove) {
                                var assignmentMove = (AssignmentMove) move;
                                var newValue = assignmentMove.getNewValue();
                                var strValue = newValue == 0 ? "" : String.valueOf(newValue);
                                table.getModel().setValueAt(strValue, assignmentMove.getRow(), assignmentMove.getCol());
                            }
                        }
                    }
                };

                clearButton.setEnabled(false);
                solveButton.setEnabled(false);
                solvedLabel.setText("Solving...");
                table.getModel().setEditable(false);
                solver.execute();
            }
        });

        clearButton.addActionListener(new TableActionListener() {
            @Override
            void actionPerformed() {
                table.clear();
                slider.setEnabled(true);
                table.getModel().setEditable(true);
                solveButton.setEnabled(true);
                solvedLabel.setText("Ready...");
            }
        });

        stopButton.addActionListener(e -> {
            if (solver != null) {
                solver.cancel(true);
                solvedLabel.setText("Stopped");
            }
        });

        frame.add(new JLabel("Solving Speed:"));
        frame.add(slider);
        frame.add(solveButton);
        frame.add(clearButton);
        frame.add(stopButton);
        frame.add(solvedLabel);

        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
    }

    /**
     * Creates a {@link JMenuBar} for the frame.
     *
     * @return a new {@link JMenuBar} object.
     */
    private JMenuBar createMenuBar() {
        var menuBar = new JMenuBar();

        var fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');

        var loadFromFile = new JMenuItem("Load...");
        var saveToFile = new JMenuItem("Save...");
        var exitMenuItem = new JMenuItem("Exit");

        loadFromFile.addActionListener(new TableActionListener() {
            @Override
            void actionPerformed() {
                var fileChooser = new JFileChooser();
                var filter = new FileNameExtensionFilter("CSV files", "csv");
                fileChooser.addChoosableFileFilter(filter);
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setMultiSelectionEnabled(false);
                fileChooser.setFileFilter(filter);

                var response = fileChooser.showOpenDialog(frame);
                if (response == JFileChooser.APPROVE_OPTION) {
                    try {
                        table.load(fileChooser.getSelectedFile().toURI());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "Error while trying to read the file:\n" + ex.getMessage(),
                            "Error reading file", JOptionPane.ERROR_MESSAGE);
                    }
                }

                table.getModel().setEditable(true);
                solveButton.setEnabled(true);
            }
        });

        saveToFile.addActionListener(new TableActionListener() {
            @Override
            void actionPerformed() {
                var fileChooser = new JFileChooser();
                var filter = new FileNameExtensionFilter("CSV files", "csv");
                fileChooser.addChoosableFileFilter(filter);
                fileChooser.setFileFilter(filter);
                fileChooser.setMultiSelectionEnabled(false);
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                var response = fileChooser.showSaveDialog(frame);
                if (response == JFileChooser.APPROVE_OPTION) {
                    var selectedFile = fileChooser.getSelectedFile();
                    if (selectedFile.exists()) {
                        var option = JOptionPane.showConfirmDialog(frame, "The selected file will be overwritten. Do you want to continue?",
                            "Overwrite File?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (option != JOptionPane.YES_OPTION) {
                            return;
                        }
                    }

                    var model = table.getModel();
                    try {
                        model.save(fileChooser.getSelectedFile());
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(frame, "Unable to save the file: " + ex.getMessage(),
                            "Unable to save", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        exitMenuItem.addActionListener(e -> frame.dispose());
        exitMenuItem.setMnemonic('x');

        fileMenu.add(loadFromFile);
        fileMenu.add(saveToFile);
        fileMenu.add(new JSeparator());
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);
        return menuBar;
    }

    private abstract class TableActionListener implements ActionListener {
        @Override
        public final void actionPerformed(ActionEvent actionEvent) {
            var editor = table.getCellEditor();
            if (editor != null) {
                editor.stopCellEditing();
            }
            actionPerformed();
        }

        abstract void actionPerformed();
    }
}
