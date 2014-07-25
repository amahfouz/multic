package com.mahfouz.multic.core;

/**
 * Combination of four cells that, if filled by a player,
 * constitute a win.
 */
public final class Four {

    public static final int VALUE_WIN = 100000000;
    public static final int VALUE_FORBIDDEN_FACTOR = 2;
    public static final int VALUE_THREE_IN_A_ROW = 16;

    private static final int VALUE_MIXED_OR_EMPTY_ROW = 0;
    private static final int VALUE_ONE_IN_A_ROW = 1;
    private static final int VALUE_TWO_IN_A_ROW = 4;

    // indices into the BoardDef.LABELS array
    private final int indices[];

    public Four(int[] indices) {
        if ((indices == null) || indices.length != 4)
            throw new IllegalArgumentException();

        this.indices = indices;
    }

    public int[] getIndices() {
        return indices;
    }

    /**
     * Evaluates the favorability of the state of the 'Four' in the
     * following way:
     *
     * - All four cell values are PC --> VALUE_PC_WIN
     * - All four cell values are Player --> VALUE_PLAYER_WIN
     * - Three cells
     *
     * @param cellValues values of cells (parallels BoardDef.LABELS)
     *
     * @return a positive value to indicate the row state is favorable to
     *         computer, negative value to indicate position is favorable
     *         to player. The larger the value, the more favorable.
     */
    public int evaluate(Player[] cellValues) {
        int numPc = 0;
        int numHuman = 0;
        for (int i = 0; i < indices.length; i++) {
            if (cellValues[indices[i]] == Player.COMPUTER)
                numPc++;
            else if (cellValues[indices[i]] == Player.HUMAN)
                numHuman++;
        }

        return eval(numPc, numHuman);
    }

    public Player getValueIfAllSame(Player[] cellValues) {
        return (cellValues[indices[0]] == cellValues[indices[1]])
            && (cellValues[indices[0]] == cellValues[indices[2]])
            && (cellValues[indices[0]] == cellValues[indices[3]])
            ? cellValues[indices[0]]
            : null;
    }

    public int getFirstEmptyIndex(Player[] cellValues) {
        for (int i = 0; i < indices.length; i++) {
            if (cellValues[indices[i]] == null)
                return indices[i];
        }

        return -1;
    }

    //
    // private methods
    //

    /**
     * Evaluates a four (from the point of view of player 1) given
     * number of cells occupied by player 1 and player 2
     */
    private int eval(int numP1, int numP2) {
        switch (numP1) {
            case 0:
                switch (numP2) {
                    case 0:
                        return VALUE_MIXED_OR_EMPTY_ROW;
                    case 1:
                        return - VALUE_ONE_IN_A_ROW;
                    case 2:
                        return - VALUE_TWO_IN_A_ROW;
                    case 3:
                        return - VALUE_THREE_IN_A_ROW;
                    case 4:
                        return - VALUE_WIN;
                    default:
                        throw new IllegalArgumentException();
                }

            case 1:
                return (numP2 == 0)
                    ? VALUE_ONE_IN_A_ROW
                    : VALUE_MIXED_OR_EMPTY_ROW;

            case 2:
                return (numP2 == 0)
                    ? VALUE_TWO_IN_A_ROW
                    : VALUE_MIXED_OR_EMPTY_ROW;

            case 3:
                return (numP2 == 0)
                    ? VALUE_THREE_IN_A_ROW
                    : VALUE_MIXED_OR_EMPTY_ROW;

            case 4:
                return VALUE_WIN;

            default:
                throw new IllegalArgumentException();
        }
    }

    public boolean isTerminalPosition(Player[] cellValues) {
        int value = evaluate(cellValues);
        return isTerminalPosition(value);
    }
    //
    // static methods
    //

    public static boolean isTerminalPosition(int valueForFour) {
        return Math.abs(valueForFour) >= VALUE_WIN;
    }

//    public static boolean isThreeInARow(int valueForFour) {
//        return Math.abs(valueForFour) == VALUE_THREE_IN_A_ROW;
//    }
}
