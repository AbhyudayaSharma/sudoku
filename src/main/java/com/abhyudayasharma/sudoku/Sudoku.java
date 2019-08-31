package com.abhyudayasharma.sudoku;

import com.abhyudayasharma.sudoku.ui.SudokuTable;
import com.abhyudayasharma.sudoku.ui.SudokuTableModel;
import net.miginfocom.swing.MigLayout;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;
import java.awt.Font;
import java.io.IOException;

public class Sudoku {
    /**
     * The {@link Font} that must be used for the sudoku board.
     */
    public static final Font BOARD_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 36);

    private final JFrame frame = new JFrame("Sudoku");
    private final SudokuTable table = new SudokuTable();

    void start() {
        initFrame();
    }

    /**
     * Initializes a {@link JFrame} for display.
     */
    private void initFrame() {
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new MigLayout());
        frame.add(table, "grow");
        frame.setJMenuBar(createMenuBar());

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

        loadFromFile.addActionListener(e -> {
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
        });

        saveToFile.addActionListener(e -> {
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

                TableModel o = table.getModel();
                if (o instanceof SudokuTableModel) {
                    var model = (SudokuTableModel) o;
                    try {
                        model.save(fileChooser.getSelectedFile());
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(frame, "Unable to save the file: " + ex.getMessage(),
                            "Unable to save", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        fileMenu.add(loadFromFile);
        fileMenu.add(saveToFile);
        menuBar.add(fileMenu);
        return menuBar;
    }
}
