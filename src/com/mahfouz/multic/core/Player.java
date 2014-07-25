package com.mahfouz.multic.core;

/**
 * Player, either human or computer.
 */
public enum Player {

    COMPUTER(Four.VALUE_WIN),
    HUMAN(- Four.VALUE_WIN);

    private final int valueOfFourInRow;

    private Player(int valueOfFourInRow) {
        this.valueOfFourInRow = valueOfFourInRow;
    }

    public int valueOfFour() {
        return valueOfFourInRow;
    }
}