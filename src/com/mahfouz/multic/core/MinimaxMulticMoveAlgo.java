package com.mahfouz.multic.core;

import com.mahfouz.multic.util.MulticLog;


/**
 * Minimax algorithm for choosing the next move.
 */
public final class MinimaxMulticMoveAlgo implements MulticMoveAlgo {

    private static final int MAX_MAX_DEPTH = 5;

    private final int maxDepth;
    private final MulticLog log;

    private MutableGameState gameState;

    private Move.Immutable moveToMake;

    public MinimaxMulticMoveAlgo(int maxDepth, MulticLog log) {
        if (log == null)
            throw new IllegalArgumentException();

        if (maxDepth > MAX_MAX_DEPTH)
            throw  new IllegalArgumentException();

        this.maxDepth = maxDepth;
        this.log = log;
    }

    //
    // public.
    //

    public Move findNextMove(MutableGameState gameState) {
        this.gameState = gameState;
        this.moveToMake = null;

        evalNode(maxDepth, true);

        // after evaluation moveToMake holds best move

        return moveToMake;
    }

    private int evalNode(int remainingDepth, boolean isMaximizing) {

        // check if terminal (winning) position

        Player player = gameState.getWinnerIfAny();
        if (player != null)
            return addDepthToValue(player.valueOfFour(), remainingDepth);

        // check if leaf node

        if (remainingDepth == 0)
            return addDepthToValue(gameState.evalBoardState(), remainingDepth);

        // iterate over all possible moves

        MoveIterator iter = new MoveIterator(gameState);

//        log.debug((isMaximizing ? "MAX" : "min") + " (" + remainingDepth +")");

        // check if terminal (draw) position
        if (! iter.hasNext())
            return 0; // game drawn

        // iterate over all possible moves finding min or max

        int bestOutcome = isMaximizing
            ? Integer.MIN_VALUE
            : Integer.MAX_VALUE;

        Move.Mutable bestMove = new Move.Mutable();
        Move.Mutable mutableMove = new Move.Mutable();

        while (iter.hasNext()) {
            iter.getNext(mutableMove);

            // snapshot the state before making a move
            MutableGameState.Memento undoInfo = gameState.makeMove(mutableMove);

            // recurse into child node
            int val = evalNode(remainingDepth - 1, ! isMaximizing);

//            log.debug(val + " (" + remainingDepth + ") " + mutableMove);

            // restore the state before trying next move
            gameState.applyUndo(undoInfo);

            // check if this value is "better", while randomizing the
            // choice if it is the same as the current best
            if ((isMaximizing && (val > bestOutcome))
                || ((! isMaximizing) && (val < bestOutcome))
                || (val == bestOutcome && Math.random() > 0.5)) {
                bestOutcome = val;
                mutableMove.copyInto(bestMove);
            }
        }

        // if at root of eval tree, record best move

        if (remainingDepth == maxDepth)
            moveToMake = bestMove.toImmutable();

        return bestOutcome;
    }

    private int addDepthToValue(int nodeValue, int remainingDepth) {

        // faster win is better, so penalize the depth while
        // keeping the value >= Four.VALUE_WIN

        if (nodeValue == Four.VALUE_WIN)
            return nodeValue + remainingDepth;

        // further loss is better, so nodes with smaller depth are a worse
        // option  (while keeping the absolute value >= Four.VALUE_WIN)

        if (nodeValue == -Four.VALUE_WIN)
            return nodeValue - remainingDepth;

        return nodeValue;
    }
}