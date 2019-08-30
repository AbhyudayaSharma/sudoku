package com.abhyudayasharma.sudoku;

import com.abhyudayasharma.sudoku.ui.SudokuTable;
import net.miginfocom.swing.MigLayout;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;
import java.awt.Font;

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
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');

        JMenuItem loadFromFile = new JMenuItem("Load...");
        JMenuItem saveToFile = new JMenuItem("Save...");

        loadFromFile.addActionListener(e -> {
            // TODO respond to mouse click
        });

        saveToFile.addActionListener(e -> {
            // TODO respond to mouse click
        });

        fileMenu.add(loadFromFile);
        fileMenu.add(saveToFile);
        menuBar.add(fileMenu);
        return menuBar;
    }
}
