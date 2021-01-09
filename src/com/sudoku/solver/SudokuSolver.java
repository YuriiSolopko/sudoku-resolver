package com.sudoku.solver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SudokuSolver {

    public int solve(Sudoku sudoku) {
        validateSudoku(sudoku);
        long start = System.currentTimeMillis();
        int iterations = runIterations(sudoku);
        long end = System.currentTimeMillis();
        System.out.println("Solving took " + (end - start) + " milliseconds");
        return iterations;
    }

    private int runIterations(Sudoku sudoku) {
        int iterations = 0;
        int consecutiveIneffectiveIterations = 0;
        while (!sudoku.isSolved()) {
            if (!runIteration(sudoku)) {
                consecutiveIneffectiveIterations++;
            } else {
                consecutiveIneffectiveIterations = 0;
            }
            if (consecutiveIneffectiveIterations > 2 && makeGuess(sudoku)) {
                consecutiveIneffectiveIterations = 0;
            }
            if (++iterations >= 100000 || consecutiveIneffectiveIterations > 2) {
                break;
            }
        }
        return iterations;
    }

    private boolean runIteration(Sudoku sudoku) {
        boolean isChanged = checkHorizontal(sudoku);
        isChanged = checkVertical(sudoku) || isChanged;
        return checkCellBlocks(sudoku) || isChanged;
    }

    private boolean makeGuess(Sudoku sudoku) {
        Sudoku copy = sudoku.copy();
        Cell selectedCell = null;
        List<Integer> unsolvedValues = new ArrayList<>();
        for (List<Cell> block : copy.getCellBlocks()) {
            long size = block.stream().filter(Cell::isNotSolved).count();
            if (size == 2L) {
                unsolvedValues.addAll(findUnsolvedValuesInCells(block));
                selectedCell = block.stream().filter(Cell::isNotSolved).findFirst().get();
                selectedCell.setValue(unsolvedValues.get(0));
                break;
            }
        }

        if (selectedCell != null) {
            boolean isFail = false;
            while (!copy.isSolved()) {
                try {
                    runIterations(copy);
                } catch (IllegalArgumentException exception) {
                    isFail = true;
                    break;
                }

            }
            Coordinates coordinates = copy.getCellCoordinates(selectedCell);
            sudoku.setValue(coordinates.getX(), coordinates.getY(), unsolvedValues.get(isFail ? 1 : 0));
        }

        return selectedCell != null;
    }

    private boolean checkHorizontal(Sudoku sudoku) {
        return sudoku.getRows().stream().map(this::processCellGroup).anyMatch(bool -> bool);
    }

    private boolean checkVertical(Sudoku sudoku) {
        return sudoku.getColumns().stream().map(this::processCellGroup).anyMatch(bool -> bool);
    }

    private boolean checkCellBlocks(Sudoku sudoku) {
        boolean isChanged = sudoku.getCellBlocks().stream().map(this::processCellGroup).anyMatch(bool -> bool);
        return sudoku.getCellBlocks().stream().map(block -> checkForValuesInLine(sudoku, block)).anyMatch(bool -> bool) || isChanged;
    }

    private boolean checkForValuesInLine(Sudoku sudoku, List<Cell> cells) {
        Set<Integer> unsolvedValues = findUnsolvedValuesInCells(cells);
        boolean isChanged = false;
        for (Integer possibleValue : unsolvedValues) {
            List<Cell> tempCells = cells.stream().filter(cell -> cell.isPossibleValue(possibleValue)).collect(Collectors.toList());
            List<Coordinates> coords = tempCells.stream().map(sudoku::getCellCoordinates).collect(Collectors.toList());
            if (!coords.isEmpty() && coords.stream().map(Coordinates::getX).allMatch(x -> coords.get(0).getX() == x)) {
                // column
                List<Cell> column = sudoku.getColumn(coords.get(0).getX());
                column.removeAll(cells);
                isChanged = column.stream().filter(Cell::isNotSolved).map(cell -> cell.removeFromPossibleValues(possibleValue)).anyMatch(bool -> bool);
            } else if (!coords.isEmpty() && coords.stream().map(Coordinates::getY).allMatch(y -> coords.get(0).getY() == y)) {
                // row
                List<Cell> row = sudoku.getRow(coords.get(0).getY());
                row.removeAll(cells);
                isChanged = row.stream().filter(Cell::isNotSolved).map(cell -> cell.removeFromPossibleValues(possibleValue)).anyMatch(bool -> bool);
            }
        }
        return isChanged;
    }

    private boolean processCellGroup(List<Cell> cells) {
        boolean isChanged = removeSolvedValuesFromCells(cells, findSolvedValues(cells));
        return findSinglePossibleValuesInCells(cells) || isChanged;
    }

    private boolean findSinglePossibleValuesInCells(List<Cell> cells) {
        boolean isFound = false;
        Set<Integer> unsolvedValues = findUnsolvedValuesInCells(cells);
        for (Integer value : unsolvedValues) {
            List<Cell> possibleCells = cells.stream().filter(cell -> cell.isPossibleValue(value)).collect(Collectors.toList());
            if (possibleCells.size() == 1) {
                possibleCells.get(0).setValue(value);
                isFound = true;
            }
        }
        return isFound;
    }

    private Set<Integer> findSolvedValues(List<Cell> cells) {
        return cells.stream().filter(Cell::isSolved).map(Cell::getValue).collect(Collectors.toSet());
    }

    private boolean removeSolvedValuesFromCells(List<Cell> cells, Set<Integer> solvedValues) {
        return cells.stream()
                .filter(Cell::isNotSolved)
                .map(cell -> cell.removeFromPossibleValues(solvedValues))
                .anyMatch(bool -> bool);
    }

    private Set<Integer> findUnsolvedValuesInCells(List<Cell> cells) {
        Set<Integer> result = IntStream.rangeClosed(1,9).boxed().collect(Collectors.toSet());
        result.removeAll(findSolvedValues(cells));
        return result;
    }

    private void validateSudoku(Sudoku sudoku) {
        int rowNum = 1;
        for (List<Cell> row : sudoku.getRows()) {
            Optional<Integer> duplicate = findDuplicateValue(row);
            if (duplicate.isPresent()) {
                throw new IllegalArgumentException("Duplicate value [" + duplicate.get() + "] is found in row: " + rowNum);
            }
            rowNum++;
        }

        int colNum = 1;
        for (List<Cell> column : sudoku.getColumns()) {
            Optional<Integer> duplicate = findDuplicateValue(column);
            if (duplicate.isPresent()) {
                throw new IllegalArgumentException("Duplicate value [" + duplicate.get() + "] is found in column: " + colNum);
            }
            colNum++;
        }

        int blockNum = 1;
        for (List<Cell> block : sudoku.getCellBlocks()) {
            Optional<Integer> duplicate = findDuplicateValue(block);
            if (duplicate.isPresent()) {
                throw new IllegalArgumentException("Duplicate value [" + duplicate.get() + "] is found in block: " + blockNum);
            }
            blockNum++;
        }
    }

    private Optional<Integer> findDuplicateValue(List<Cell> cells) {
        List<Integer> solvedValues = cells.stream().filter(Cell::isSolved).map(Cell::getValue).collect(Collectors.toList());
        Set<Integer> valuesSet = new HashSet<>();
        for (Integer value : solvedValues) {
            if (!valuesSet.add(value)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }
}
