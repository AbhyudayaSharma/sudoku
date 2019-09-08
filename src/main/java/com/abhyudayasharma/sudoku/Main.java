package com.abhyudayasharma.sudoku;

import lombok.extern.slf4j.Slf4j;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

@Slf4j
public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            log.warn("Unable to set Nimbus Look and Feel for the application.", e);
        }

        SwingUtilities.invokeLater(() -> new Sudoku().start());
    }
}
