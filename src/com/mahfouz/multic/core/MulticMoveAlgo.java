package com.mahfouz.multic.core;


/**
 * Algorithm to pick the computer move given a position.
 */
public interface MulticMoveAlgo {

    /**
     * Find the next move given the current game state.
     */
    Move findNextMove(MutableGameState curGameState);

    /**
     * Factory to create an algorithm for the specified difficulty level.
     */
    interface Factory {
        MulticMoveAlgo create(Difficulty difficulty);
    }
}
