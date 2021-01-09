package com.sudoku.solver;

public class Main {

    public static void main(String[] args) {
        Sudoku sudoku = Sudoku.builder()
                .addValue(1,1, 4)
                .addValue(4,1, 1)
                .addValue(6,1, 3)

                .addValue(2,2, 9)
                .addValue(3,2, 2)
                .addValue(4,2, 8)
                .addValue(6,2, 4)
                .addValue(8,2, 6)

                .addValue(1,3, 5)
                .addValue(9,3, 9)

                .addValue(5,4, 1)

                .addValue(2,5, 6)
                .addValue(3,5, 1)
                .addValue(9,5, 4)

                .addValue(4,6, 9)
                .addValue(5,6, 2)
                .addValue(6,6, 5)

                .addValue(1,7, 2)
                .addValue(5,7, 6)
                .addValue(7,7, 9)
                .addValue(9,7, 3)

                .addValue(5,8, 8)
                .addValue(8,8, 7)

                .addValue(7,9, 1)
                .addValue(8,9, 5)
                .build();
        System.out.println(sudoku);
        SudokuSolver sudokuSolver = new SudokuSolver();
        int counter = sudokuSolver.solve(sudoku);
        System.out.println("Solved in " + counter + " iterations");
        System.out.println(sudoku);
    }

}
