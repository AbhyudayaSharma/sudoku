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

import com.abhyudayasharma.sudoku.Sudoku;
import com.abhyudayasharma.sudoku.SudokuBoard;

import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * A {@link javax.swing.CellEditor} that validates input for a sudoku cell and sets the font specified by
 * {@link Sudoku#BOARD_FONT}.
 *
 * @author Abhyudaya Sharma
 */
class SudokuCellEditor extends DefaultCellEditor {
    SudokuCellEditor() {
        super(new SudokuTextField());
    }

    private static class SudokuTextField extends JTextField {
        SudokuTextField() {
            setFont(Sudoku.BOARD_FONT);
            setHorizontalAlignment(CENTER);

            getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent documentEvent) {
                    checkAndWarn();
                }

                @Override
                public void removeUpdate(DocumentEvent documentEvent) {
                    checkAndWarn();
                }

                @Override
                public void changedUpdate(DocumentEvent documentEvent) {
                    checkAndWarn();
                }

                /**
                 * Validates the input as a possible number for a sudoku square.
                 * <p>
                 * If the integer parsed from the text of the {@link JTextField} is valid, the text is left unmodified.
                 * Otherwise, the text is set to an empty string. If the parsed number was not in range for the puzzle,
                 * a message box is generated.
                 */
                private void checkAndWarn() {
                    final var text = SudokuTextField.this.getText();

                    // blank input is invalid; empty is fine
                    if (text.isEmpty()) {
                        return;
                    }

                    var parsedInt = 0;
                    var doClearTextField = false;
                    try {
                        parsedInt = Integer.parseInt(text);
                    } catch (NumberFormatException e) {
                        doClearTextField = true;
                    }

                    if (!doClearTextField && (parsedInt > SudokuBoard.SIZE || parsedInt <= 0)) {
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                            SudokuTextField.this.getTopLevelAncestor(),
                            String.format("Please enter an integer between %d and %d", 1, SudokuBoard.SIZE),
                            "Invalid input", JOptionPane.WARNING_MESSAGE));

                        doClearTextField = true;
                    }

                    if (doClearTextField) {
                        SwingUtilities.invokeLater(() -> SudokuTextField.this.setText(""));
                    }
                }
            });
        }
    }
}
