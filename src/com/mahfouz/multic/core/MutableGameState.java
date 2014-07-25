package com.mahfouz.multic.core;

import com.mahfouz.multic.uim.XGameGridUiModel;
import com.mahfouz.multic.util.MulticLog;

/**
 * Aggregated mutable state of the game:
 * - Board state (contents of cells)
 * - Knob states
 * - Who's turn is it
 */
public final class MutableGameState {

    private final Knobs knobs;
    private final BoardState boardState;
    private final Difficulty difficulty;
    private final MulticMoveAlgo algo;

    private boolean isComputerTurn;

    private MutableGameState(Knobs knobs,
                             BoardState boardState,
                             Difficulty difficulty,
                             boolean isComputerTurn,
                             MulticLog log) {
        this.knobs = knobs;
        this.boardState = boardState;
        this.difficulty = difficulty;
        this.isComputerTurn = isComputerTurn;
        this.algo = createAlgo(log);
    }

    public static MutableGameState createInit
        (boolean randomFirstPos,
         Difficulty difficulty,
         boolean computerStarts,
         MulticLog log) {

        return new MutableGameState
            (Knobs.createInit(randomFirstPos),
             new BoardState(),
             difficulty,
             computerStarts,
             log);
    }

    //
    // public getters
    //

    public Knobs getKnobs() {
        return knobs;
    }

    public boolean isComputerTurn() {
        return this.isComputerTurn;
    }

    public Player getWinnerIfAny() {
        return boardState.getWinnerIfAny(knobs.getCurProduct());
    }

    public int[] getWinningFourIfAny() {
        return boardState.getWinningFourIfAny();
    }

    public MulticGameCell getCellState(Knob.Location knobLoc, int pos) {
        return boardState.getCellState
            (knobs.getProdIfMoved(pos, knobLoc));
    }

    public boolean isValidMove(Knob.Location knobLoc, int pos) {
        return boardState.isEmptyCell
            (knobs.getProdIfMoved(pos, knobLoc));
    }

    public int evalBoardState() {
        // perform more sophisticated evaluation for EXPERT level
        return boardState.evaluate(difficulty.equals(Difficulty.EXPERT));
    }

    public XGameGridUiModel getGridModel() {
        return boardState;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Move findNextComputerMove() {
        return algo.findNextMove(this);
    }

    //
    // public mutators
    //

    public Memento makeMove(Move move) {

        boolean wasComputerTurnBeforeMove = isComputerTurn;
        int posBeforeMove = knobs.getPosFor(move.getKnobLoc());

        knobs.set(move.getKnobLoc(), move.getPos());

        int product = knobs.getCurProduct();

        Player cellValue = isComputerTurn
            ? Player.COMPUTER
            : Player.HUMAN;

        boardState.fillCell(product, cellValue);

        this.isComputerTurn = ! isComputerTurn;

        return new Memento
            (product,
             new Move.Immutable(posBeforeMove, move.getKnobLoc()),
             wasComputerTurnBeforeMove);
    }

    public void applyUndo(Memento undoInfo) {
        undoInfo.apply();
    }

    //
    // private
    //

    private MulticMoveAlgo createAlgo(MulticLog log) {

        // level to depth
        // EASY -> 1
        // MEDIUM -> 2
        // HARD -> 3
        // EXPERT -> 4
        // INSANE -> 5

        int maxDepth = difficulty.ordinal();
        MulticMoveAlgo random = new MulticRandomAlgo();
        MulticMoveAlgo minmax = new MinimaxMulticMoveAlgo(maxDepth, log);

        switch (difficulty) {
            case SILLY:
                return random;

            case EASY:
                return new AlternatingMutlicMoveAlgo
                    (minmax, random, 1);

            case MEDIUM:
                return new AlternatingMutlicMoveAlgo
                    (minmax, random, 4);

            case HARD:
            case EXPERT:
                return minmax;

            default:
                return minmax;
        }
    }

    //
    // nested
    //

    public final class Memento {

        private final int productCellToEmpty;
        private final Move reverseMove;
        private final boolean isComputerTurn;

        public Memento(int productCellToEmpty,
                       Move reverseMove,
                       boolean isComputerTurn) {
            this.productCellToEmpty = productCellToEmpty;
            this.reverseMove = reverseMove;
            this.isComputerTurn = isComputerTurn;
        }

        public int getCellIndex() {
            return BoardDef.getIndexForProduct(productCellToEmpty);
        }

        private void apply() {
            boardState.fillCell(productCellToEmpty, null);
            knobs.set(reverseMove.getKnobLoc(), reverseMove.getPos());
            MutableGameState.this.isComputerTurn = isComputerTurn;
        }
    }
}
