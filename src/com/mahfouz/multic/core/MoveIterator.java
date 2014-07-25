/*
 * CONFIDENTIAL
 * Copyright 2013 Webalo, Inc.  All rights reserved.
 */

package com.mahfouz.multic.core;

/**
 * Enumerates valid moves given knob states and cell states.
 */
public final class MoveIterator {

    private final MutableGameState game;
    private Knob.Location curKnob;
    private int curPos;

    public MoveIterator(MutableGameState gameState) {
        if (gameState == null)
            throw new IllegalArgumentException();

        this.game = gameState;
        this.curKnob = Knob.Location.TOP;

        findNextValidMove();
    }

    public boolean hasNext() {
        return curKnob != null;
    }

    public void getNext(Move.Mutable mutableMove) {
        if (! hasNext())
            throw new IllegalStateException("No more moves.");

        mutableMove.set(curKnob, curPos);

        findNextValidMove();
    }

    //
    // private
    //

    private void findNextValidMove() {
        while (true) {
            // curPos is initially set to zero
            curPos++;

            // switch to bottom when exhausted top knob moves
            if (! Knob.isValidPos(curPos)) {
                if (curKnob == Knob.Location.TOP
                    && ! game.getKnobs().areAtSamePos()) {
                    curKnob =  Knob.Location.BOTTOM;
                    curPos = 0;
                    continue;
                }
                else {
                    // we are done when exhausted bottom know moves
                    curKnob = null;
                    break;
                }
            }

            // skip current position for knob
            // skip positions whose product is for a non-empty cell
            if (curPos != game.getKnobs().getPosFor(curKnob)
                && (game.isValidMove(curKnob, curPos)))
                break;
        }
    }
}
