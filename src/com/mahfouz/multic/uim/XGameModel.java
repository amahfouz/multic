/*
 * CONFIDENTIAL
 * Copyright 2013 Webalo, Inc.  All rights reserved.
 */

package com.mahfouz.multic.uim;

import com.mahfouz.multic.core.Difficulty;
import com.mahfouz.multic.core.Knob;
import com.mahfouz.multic.core.Move;
import com.mahfouz.multic.core.MoveIterator;
import com.mahfouz.multic.core.MutableGameState;
import com.mahfouz.multic.core.Player;
import com.mahfouz.multic.core.MulticGameCell;
import com.mahfouz.multic.util.MulticLog;

/**
 * Model for the game as seen by the UI.
 */
public final class XGameModel {

    public static final Difficulty DEFAULT_DIFFICULTY = Difficulty.MEDIUM;

    private final MutableGameState gameState;
    private final MulticLog log;

    private XGameUi gameUi;

    public XGameModel(Difficulty difficulty,
                      boolean randomFirstPos,
                      boolean humanStarts,
                      MulticLog log) {
        if (log == null)
            throw new IllegalArgumentException();

        this.gameState = MutableGameState.createInit
            (randomFirstPos, difficulty, ! humanStarts, log);
        this.log = log;
    }

    //
    // getters
    //

    public synchronized XGameGridUiModel getGridUiModel() {
        return gameState.getGridModel();
    }

    public synchronized boolean isGameInProgress() {
        return getTurnPlayer() != null;
    }

    public Difficulty getDifficulty() {
        return gameState.getDifficulty();
    }

    //
    // mutators
    //

    /**
     * Register UI when ready to receive commands.
     */
    public void registerUi(XGameUi gameUiIfReady) {
        String action = (gameUiIfReady == null)
            ? "Unregistering"
            : "Registering";
        log.debug(action + " game UI.");

        this.gameUi = gameUiIfReady;

        // (re-)init state of UI

        gameUi.refreshGrid();
        gameUi.setWheelSelection
            (Knob.Location.TOP,
             getSelectedIndexFor(Knob.Location.TOP),
             false);
        gameUi.setWheelSelection
            (Knob.Location.BOTTOM,
             getSelectedIndexFor(Knob.Location.BOTTOM),
             false);

        updateViewState();

        // start the action if it is computer's turn

        if (getTurnPlayer() == Player.COMPUTER)
            findComputerMove();
    }

    public synchronized void handleKnobChanged
        (final Knob.Location knobLoc,
         int selectedIndex) {

        // check that knob did actually change

        if (getSelectedIndexFor(knobLoc) == selectedIndex)
            return;

        // perform safety checks

        if (gameUi == null)
            return;

        if (getTurnPlayer() != Player.HUMAN) {
            log.warn("Human attempted move during computer turn.");
            return;
        }

        if (gameHasEnded()) {
            log.warn("Human attempted move after game was over.");
            return;
        }

        // disallow further moves during "thinking" time
        setKnobEnablement(false);

        int factor = Knob.factorForPos(selectedIndex);

        MulticGameCell cellToFill = gameState.getCellState(knobLoc, factor);

        if (! cellToFill.isEmpty()) {
            CompletionListener listener = new CompletionListener() {
                public void done() {
                    gameUi.setWheelSelection
                        (knobLoc, getSelectedIndexFor(knobLoc), true);
                    updateViewState();
                }
            };

            gameUi.flashCells
                (new int[] {cellToFill.getCellIndex()}, true, listener);
        }
        else {
            // make the move
            MutableGameState.Memento stateBeforeUserMove
                = gameState.makeMove(new Move.Immutable(factor, knobLoc));

            CompletionListener listener = new CompletionListener() {
                public void done() {
                    log.debug("Received done notification after cell update.");
                    updateViewState();
                    if (! gameHasEnded())
                        findComputerMove();
                }
            };

            gameUi.updateCell(stateBeforeUserMove.getCellIndex(), listener);
        }
    }

    //
    // private
    //

    private void updateViewState() {
        if (gameUi == null)
            return;

        Player nextTurn = getTurnPlayer();

        log.info("Updating view. Turn = " + nextTurn);

        boolean knobsAreEnabled = (nextTurn == Player.HUMAN);
        log.debug("Setting knob enablement to " + knobsAreEnabled);
        setKnobEnablement(knobsAreEnabled);

        if (nextTurn == null) {
            // game has ended

            Player winnerIfAny = getWinnerIfAny();

            if (winnerIfAny == Player.HUMAN)
                announceWinAndPromptRestart("You win!", Player.HUMAN);
            else if (winnerIfAny == Player.COMPUTER)
                announceWinAndPromptRestart("Computer wins!", Player.COMPUTER);
            else {
                gameUi.showMessage("Game drawn!", Player.HUMAN);
                gameUi.showPlayAgainControl();
            }
        }
        else {
            String msg = (nextTurn == Player.COMPUTER)
                ? "Thinking..."
                : "Your turn!";
            gameUi.showMessage(msg, nextTurn);
        }
    }

    private void announceWinAndPromptRestart(String message, Player player) {
        CompletionListener listener = new CompletionListener() {
            public void done() {
                gameUi.showPlayAgainControl();
            }
        };

        gameUi.showMessage(message, player);
        gameUi.flashCells(getWinningFourIfAny(), false, listener);
    }

    private void setKnobEnablement(boolean isEnabled) {
        if (gameUi != null) {
            gameUi.setWheelEnabled(Knob.Location.TOP, isEnabled);
            gameUi.setWheelEnabled(Knob.Location.BOTTOM, isEnabled);
        }
    }

    private synchronized int getSelectedIndexFor(Knob.Location knobLoc) {
        int factor = gameState.getKnobs().getPosFor(knobLoc);
        return Knob.posForFactor(factor);
    }

    private synchronized Player getWinnerIfAny() {
        return gameState.getWinnerIfAny();
    }

    private synchronized int[] getWinningFourIfAny() {
        return gameState.getWinningFourIfAny();
    }

    /**
     * Returns the player who has to make a move, or null if the game
     * has ended (either with a win or a draw).
     */
    private synchronized Player getTurnPlayer() {
        if (gameHasEnded())
            return null;

        return gameState.isComputerTurn()
            ? Player.COMPUTER
            : Player.HUMAN;
    }

    /**
     * Check whether game has ended. Check that either:
     * - A four has been formed by the last move.
     * - No more moves are possible.
     */
    private boolean gameHasEnded() {
        return (gameState.getWinnerIfAny() != null) || ! hasValidMoves();
    }

    private boolean hasValidMoves() {
        return new MoveIterator(gameState).hasNext();
    }

    private void findComputerMove() {
        // run algorithm asynchronously to avoid blocking caller
        new Thread(new Runnable() {
            public void run() {
                try {
                    // simulate thinking
                    if (gameState.getDifficulty().ordinal() < Difficulty.EXPERT.ordinal())
                        Thread.sleep(700);
                }
                catch (InterruptedException ex) {}

                performComputerMove();
            }
        }).start();
    }

    /**
     * Performs computer move.
     */
    private synchronized void performComputerMove() {
        Move computerMove = gameState.findNextComputerMove();
        MutableGameState.Memento stateBeforeComputerMoved
            = gameState.makeMove(computerMove);

        log.debug("Current board state = " + gameState.evalBoardState());

        Knob.Location knobLoc = computerMove.getKnobLoc();
        gameUi.setWheelSelection
            (knobLoc, getSelectedIndexFor(knobLoc), true);
        gameUi.updateCell
            (stateBeforeComputerMoved.getCellIndex(),
             new CompletionListener() {
                public void done() {
                    updateViewState();
                }
             });
    }
}
