package com.mahfouz.multic.core;

/**
 * Hybrid algorithm that alternates between two algorithms.
 */
public final class AlternatingMutlicMoveAlgo implements MulticMoveAlgo {

    private final MulticMoveAlgo firstAlgo;
    private final MulticMoveAlgo secondAlgo;
    private final int numTimesToRunFirstAlgoBeforeAlternating;

    private int remainingCounter;

    public AlternatingMutlicMoveAlgo
        (MulticMoveAlgo firstAlgo,
         MulticMoveAlgo secondAlgo,
         int numTimesToRunFirstAlgoBeforeAlternating) {

        if (firstAlgo == null  || secondAlgo == null)
            throw new IllegalArgumentException();

        this.firstAlgo = firstAlgo;
        this.secondAlgo = secondAlgo;
        this.numTimesToRunFirstAlgoBeforeAlternating
            = numTimesToRunFirstAlgoBeforeAlternating;
        this.remainingCounter = numTimesToRunFirstAlgoBeforeAlternating;
    }

    public Move findNextMove(MutableGameState curGameState) {
        if (remainingCounter == 0) {
            remainingCounter
                = numTimesToRunFirstAlgoBeforeAlternating;
            return secondAlgo.findNextMove(curGameState);
        }

        --remainingCounter;
        return firstAlgo.findNextMove(curGameState);
    }
}
