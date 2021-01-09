package com.sudoku.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Sudoku {
    private Cell[][] sudokuCells;

    public Sudoku() {
        initializeCells();
    }

    public Sudoku(Cell[][] sudokuCells) {
        this.sudokuCells = sudokuCells;
    }

    private void initializeCells() {
        sudokuCells = new Cell[9][9];
        for (int i = 0; i < sudokuCells[0].length; i++) {
            for (int j = 0; j < sudokuCells.length; j++) {
                sudokuCells[j][i] = new Cell();
            }
        }
    }

    public void setValue(int i, int j, Integer value) {
        if (i < 0 || j < 0 || i >= sudokuCells.length || j > sudokuCells[0].length) {
            throw new IllegalArgumentException("Invalid coordinates: (" + i + ", " + j + ")");
        }
        sudokuCells[i][j].setValue(value);
    }

    public List<List<Cell>> getRows() {
        List<List<Cell>> result = new ArrayList<>();
        for (int i = 0; i < sudokuCells[0].length; i++) {
            result.add(getRow(i));
        }
        return result;
    }

    public List<List<Cell>> getColumns() {
        List<List<Cell>> result = new ArrayList<>();
        for (int i = 0; i < sudokuCells.length; i++) {
            result.add(getColumn(i));
        }
        return result;
    }

    public List<List<Cell>> getCellBlocks() {
        List<List<Cell>> result = new ArrayList<>();
        for (int j = 0; j < sudokuCells[0].length / 3; j++) {
            for (int i = 0; i < sudokuCells.length / 3; i++) {
                result.add(getCellBlock(i, j));
            }
        }
        return result;
    }

    public List<Cell> getRow(int row) {
        List<Cell> result = new ArrayList<>();
        for (Cell[] sudokuCell : sudokuCells) {
            result.add(sudokuCell[row]);
        }
        return result;
    }

    public List<Cell> getColumn(int column) {
        return new ArrayList<>(Arrays.asList(sudokuCells[column]));
    }

    public Sudoku copy() {
        Cell[][] sudokuCellsCopy = new Cell[sudokuCells.length][sudokuCells[0].length];
        for (int j = 0; j < sudokuCells[0].length; j++) {
            for (int i = 0; i < sudokuCells.length; i++) {
                sudokuCellsCopy[i][j] = sudokuCells[i][j].copy();
            }
        }
        return new Sudoku(sudokuCellsCopy);
    }

    /**
     * _________
     * | 0 1 2 |
     * | 3 4 5 |
     * | 6 7 8 |
     * ‾‾‾‾‾‾‾‾‾
     * @return Cell list in block 3 x 3 with indexes showed above
     */
    public List<Cell> getCellBlock(int x, int y) {
        List<Cell> result = new ArrayList<>();
        for (int j = y * 3; j < (y + 1) * 3; j++) {
            for (int i = x * 3; i < (x + 1) * 3; i++) {
                result.add(sudokuCells[i][j]);
            }
        }
        return result;
    }

    public Coordinates getCellCoordinates(Cell cell) {
        for (int j = 0; j < sudokuCells[0].length; j++) {
            for (int i = 0; i < sudokuCells.length; i++) {
                if (sudokuCells[i][j] == cell) {
                    return Coordinates.of(i, j);
                }
            }
        }
        throw new IllegalArgumentException("Cell not found: " + cell);
    }

    public boolean isSolved() {
        return getColumns()
                .stream()
                .flatMap(Collection::stream)
                .allMatch(Cell::isSolved);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int rowNumber = 0;
        for (List<Cell> row : getRows()) {
            List<String> stringRow = row.stream().map(Cell::toString).collect(Collectors.toList());
            stringRow.add(3, "|");
            stringRow.add(7, "|");
            sb.append(String.join(" ", stringRow));
            sb.append("\n");
            if (rowNumber != sudokuCells.length - 1 && rowNumber % 3 == 2) {
                sb.append("---------------------\n");
            }
            rowNumber++;
        }
        return sb.toString();
    }

    public static SudokuBuilder builder() {
        return new SudokuBuilder(new Sudoku());
    }

    public static class SudokuBuilder {

        private final Sudoku sudoku;

        public SudokuBuilder(Sudoku sudoku) {
            this.sudoku = sudoku;
        }

        public SudokuBuilder addValue(int x, int y, Integer value) {
            sudoku.setValue(x - 1 , y - 1, value);
            return this;
        }

        public Sudoku build() {
            return sudoku;
        }
    }
}
