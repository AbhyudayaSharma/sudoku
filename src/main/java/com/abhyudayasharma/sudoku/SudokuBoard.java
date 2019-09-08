package com.abhyudayasharma.sudoku;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A 9x9 matrix that can be used as a Sudoku board.
 * <p>
 * Internally, {@code 0} values are considered empty values. Values less than {@code 0} or greater than
 * {@link SudokuBoard#SIZE} are not valid.
 *
 * @author Abhyudaya Sharma
 */
@Slf4j
public class SudokuBoard {
    public static final int SIZE = 9;
    private final int[][] matrix;

    /**
     * Creates a sudoku board from a list of list os {@link String}s.
     * Empty strings are considered as empty sudoku cells.
     *
     * @param strings raw strings
     * @throws IllegalArgumentException if any of the {@link String} values is not valid in the Sudoku.
     */
    public SudokuBoard(List<List<String>> strings) {
        matrix = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                matrix[i][j] = parseValue(strings.get(i).get(j));
            }
        }
    }

    /**
     * Creates a SudokuBoard from the given matrix.
     *
     * @param matrix initialize the sudoku board from the given matrix
     */
    private SudokuBoard(int[][] matrix) {
        var isInvalid = false;
        if (matrix.length != SIZE) {
            isInvalid = true;
        }

        isInvalid = isInvalid || !Arrays.stream(matrix).mapToInt(row -> row.length).allMatch(length -> length == SIZE);
        isInvalid = isInvalid || !Arrays.stream(matrix).allMatch(
            row -> Arrays.stream(row).allMatch(x -> x >= 0 && x <= SIZE));

        if (isInvalid) {
            throw new IllegalArgumentException(String.format("The size of the matrix should be %d x %d", SIZE, SIZE));
        }

        this.matrix = matrix;
    }

    /**
     * Creates a {@link SudokuBoard} from the CSV file.
     *
     * @param uri the {@link URI} to the CSV file that will be used to initialize the sudoku board.
     * @return A {@link SudokuBoard} with values read from the CSV file
     * @throws IOException              if an I/O error takes place when trying to read the file
     * @throws IllegalArgumentException if any entry of the CSV is not a positive integer
     */
    public static SudokuBoard load(URI uri) throws IOException {
        var parser = CSVParser.parse(uri.toURL(), StandardCharsets.UTF_8, CSVFormat.RFC4180);
        int[][] matrix = new int[SIZE][SIZE];

        List<CSVRecord> records = parser.getRecords();

        for (CSVRecord record : records) {
            long recordNumber = record.getRecordNumber() - 1; // make the record number zero indexed
            if (recordNumber >= SIZE) {
                log.warn("The CSV file contains {} records, will consider only the first {} records.",
                    records.size(), SIZE);
                break;
            }

            int valueCount = 0;
            for (String value : record) {
                if (valueCount >= SIZE) {
                    log.warn("The record number {} contains {} values, will consider only the first {} values.",
                        recordNumber, record.size(), SIZE);
                    break;
                }

                matrix[(int) recordNumber][valueCount] = parseValue(value);

                valueCount++;
            }
        }

        return new SudokuBoard(matrix);
    }

    /**
     * Parse a valid sudoku integer from the given string value.
     *
     * @param value the string value to be parsed into a valid sudoku integer.
     * @return parsed integer value valid for the Sudoku
     * @throws IllegalArgumentException when string is invalid for the value of a sudoku cell
     */
    private static int parseValue(String value) {
        var parsedInt = 0;

        if (!value.isBlank()) { // blank values should mean empty sudoku squares
            try {
                parsedInt = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                    String.format("The value \"%s\" cannot be parsed as an integer", value), e);
            }

            if (parsedInt < 1 || parsedInt > SIZE) {
                throw new IllegalArgumentException(
                    String.format("The number \"%d\" is not valid as the value of a sudoku block", parsedInt));
            }
        }

        return parsedInt;
    }

    /**
     * Return the value at a particular row or a column.
     *
     * @param row the row index
     * @param col the column index
     * @return {@link Optional#empty()} if no element is present at that cell.
     * @throws IndexOutOfBoundsException if the row and column indices are out of bounds.
     */
    public Optional<Integer> get(int row, int col) {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
            throw new IndexOutOfBoundsException(String.format("Matrix Index (%d, %d) is out of bounds", row, col));
        }

        var value = matrix[row][col];
        if (value == 0) {
            return Optional.empty();
        } else {
            return Optional.of(value);
        }
    }

    /**
     * Return the sudoku as a list of list of strings.
     * <p>
     * The returned list is modifiable and a new list is generated on every call.
     *
     * @return the sudoku as a list of list of strings
     */
    public List<List<String>> asList() {
        var ret = new ArrayList<List<String>>();
        for (int[] ints : matrix) {
            ret.add(Arrays.stream(ints).mapToObj(x -> x == 0 ? "" : String.valueOf(x)).collect(Collectors.toList()));
        }
        return ret;
    }

    /**
     * Return true if the {@link SudokuBoard} is valid.
     *
     * @return true if the {@link SudokuBoard} is valid.
     */
    public boolean isValid() {
        final var rowInts = new HashSet<Integer>();
        final var colInts = new HashSet<Integer>();

        // check for for duplicates in every row and column
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                final var rowValue = matrix[i][j];
                if ((rowValue != 0 && rowInts.contains(rowValue))) {
                    return false;
                } else {
                    rowInts.add(rowValue);
                }

                final var colValue = matrix[j][i];
                if (colValue != 0 && colInts.contains(colValue)) {
                    return false;
                } else {
                    colInts.add(colValue);
                }
            }

            rowInts.clear();
            colInts.clear();
        }

        // now check the squares inside the big square.
        // for an n * n sudoku, there are n small squares inside it.
        // each sqrt(n) wide row contains sqrt(n) small squares.
        final var intsInSquare = new HashSet<Integer>();
        final var sqrt = (int) Math.rint(Math.sqrt(SIZE));

        for (int i = 0; i < sqrt; i++) {
            final var rowStart = sqrt * i;
            final var rowEnd = rowStart + sqrt; // non-inclusive

            for (int j = 0; j < sqrt; j++) {
                final var colStart = sqrt * j;
                final var colEnd = colStart + sqrt; // non-inclusive, again

                // go over every element of this square
                for (int k = rowStart; k < rowEnd; k++) {
                    for (int l = colStart; l < colEnd; l++) {
                        final var value = matrix[k][l];

                        if (value == 0) {
                            continue;
                        }

                        if (intsInSquare.contains(value)) {
                            return false;
                        } else {
                            intsInSquare.add(value);
                        }
                    }
                }

                intsInSquare.clear();
            }
        }

        return true;
    }

    /**
     * Return a deep-copy of the internal matrix used by the board.
     *
     * @return copy of the internal matrix used by this {@link SudokuBoard}
     */
    public int[][] asMatrix() {
        return Arrays.stream(matrix).map(int[]::clone).toArray(int[][]::new);
    }
}
