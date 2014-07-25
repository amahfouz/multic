/*
 * CONFIDENTIAL
 * Copyright 2014 Webalo, Inc.  All rights reserved.
 */

package com.mahfouz.multic.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Picks a move at random for the computer.
 */
public final class MulticRandomAlgo implements MulticMoveAlgo {

    public Move findNextMove(MutableGameState gameState) {
        MoveIterator iter = new MoveIterator(gameState);

        if (! iter.hasNext())
            return null;

        List<Move> validMoves = new ArrayList<Move>();
        Move.Mutable mutableMove = new Move.Mutable();
        while (iter.hasNext()) {
            iter.getNext(mutableMove);
            validMoves.add(mutableMove.toImmutable());
        }

        // pick at random

        int randIndex = (int)Math.floor(Math.random() * validMoves.size());

        return validMoves.get(randIndex);
    }
}
