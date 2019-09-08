package com.abhyudayasharma.sudoku;

import com.abhyudayasharma.sudoku.core.Result;
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
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

@Slf4j
public class Sudoku {
    /**
     * The {@link Font} that must be used for the sudoku board.
     */
    public static final Font BOARD_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 36);

    private final JFrame frame = new JFrame("Sudoku");
    private final SudokuTable table = new SudokuTable();
    private final JButton solveButton = new JButton("Solve");
    private final JLabel solvedLabel = new JLabel("Ready...");

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

        solveButton.addActionListener(new TableActionListener() {
            @Override
            void actionPerformed() {
                var board = table.getBoard();
                var solver = new SudokuSolver(board);
                solveButton.setEnabled(false);
                solvedLabel.setText("Solving...");
                table.getModel().setEditable(false);

                var sudokuSolver = new SwingWorker<Result, Integer>() {
                    @Override
                    protected Result doInBackground() {
                        solver.solve();
                        return solver.getResult().orElseThrow(() -> new IllegalStateException("Unable to get the result"));
                    }

                    @Override
                    protected void done() {
                        try {
                            final var result = get();
                            final var newModel = new SudokuTableModel(result.getBoard());
                            newModel.setEditable(false);
                            table.setModel(newModel);
                            solvedLabel.setText("Done.");
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(frame, "Unable to solve: " + e.getCause().getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                            table.getModel().setEditable(true);
                            solveButton.setEnabled(true);
                            solvedLabel.setText("Ready");
                        }
                    }

                    @Override
                    protected void process(List<Integer> chunks) {
                        // FIXME: 9/7/2019
                        super.process(chunks);
                    }
                };

                sudokuSolver.execute();
            }
        });

        var clearButton = new JButton("Clear");
        clearButton.addActionListener(new TableActionListener() {
            @Override
            void actionPerformed() {
                table.clear();
                table.getModel().setEditable(true);
                solveButton.setEnabled(true);
            }
        });

        frame.add(solveButton);
        frame.add(clearButton);
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
                solvedLabel.setText("Ready...");
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
