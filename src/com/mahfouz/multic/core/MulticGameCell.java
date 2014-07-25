/*
 * CONFIDENTIAL
 * Copyright 2014 Webalo, Inc.  All rights reserved.
 */

package com.mahfouz.multic.core;

/**
 * State of a game cell.
 */
public final class MulticGameCell {

    /** Index of cell in board (represented as an array) */
    private final int cellIndex;

    /** Occupant, or null if the cell is empty */
    private final Player occupantIfAny;

    public MulticGameCell(int cellIndex, Player occupantIfAny) {
        this.cellIndex = cellIndex;
        this.occupantIfAny = occupantIfAny;
    }

    public int getCellIndex() {
        return this.cellIndex;
    }

    public Player getOccupantIfAny() {
        return this.occupantIfAny;
    }

    public boolean isEmpty() {
        return occupantIfAny == null;
    }
}
