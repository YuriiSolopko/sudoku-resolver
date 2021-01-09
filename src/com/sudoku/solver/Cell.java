package com.sudoku.solver;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Cell {

    private Integer value;
    private Set<Integer> possibleValues;

    public Cell() {
        possibleValues = IntStream.rangeClosed(1,9).boxed().collect(Collectors.toSet());
    }

    public Cell(Integer value, Set<Integer> possibleValues) {
        this.value = value;
        this.possibleValues = possibleValues;
    }

    public void setValue(Integer value) {
        this.value = value;
        possibleValues.clear();
    }

    public Integer getValue() {
        return value;
    }

    /**
     * @param values that cannot be in this Cell
     * @return true if Cell got solved
     */
    public boolean removeFromPossibleValues(Set<Integer> values) {
        possibleValues.removeAll(values);
        if (possibleValues.isEmpty()) {
            throw new IllegalArgumentException("Possible values cannot remain empty");
        } else if (possibleValues.size() == 1) {
            value = possibleValues.iterator().next();
            possibleValues.clear();
            return true;
        }
        return false;
    }

    public boolean removeFromPossibleValues(Integer value) {
        return removeFromPossibleValues(Set.of(value));
    }

    public boolean isPossibleValue(Integer value) {
        return possibleValues != null && possibleValues.contains(value);
    }

    public boolean isSolved() {
        return value != null;
    }

    public boolean isNotSolved() {
        return !isSolved();
    }

    public Cell copy() {
        return new Cell(this.value, new HashSet<>(possibleValues));
    }

    @Override
    public String toString() {
        return value != null ? String.valueOf(value) : "_";
    }
}
