package com.abhyudayasharma.sudoku;

import org.slf4j.LoggerFactory;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            LoggerFactory.getLogger(Main.class).warn("Unable to set Nimbus Look and Feel for the application.", e);
        }

        SwingUtilities.invokeLater(() -> new Sudoku().start());
    }
}
