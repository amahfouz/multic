package com.mahfouz.multic.core;

import java.util.ArrayList;
import java.util.List;

import com.mahfouz.multic.uim.XGameGridUiModel;

/**
 * State of the game board
 */
public final class BoardState implements XGameGridUiModel {

    // for each cell in the board, contains one of Cell.VALUE_XXX values
    private final Player[] cellValues;

    public BoardState() {
        this.cellValues = new Player[BoardDef.NUM_CELLS];
    }

    /**
     * Evaluates the current position.
     *
     * At least considers:
     *   - State of fours
     *
     * Depending on difficulty level may consider:
     *   - Overlaps between fours (two fours completed by the same cell)
     *   - Knob values that the other player can't use or else he would
     *     lose immediately (those that are a factor in a cell which
     *     would complete a four that is currently three-in-a-row)
     *
     * @return a positive value to indicate position is favorable to
     *         computer, negative value to indicate position is favorable
     *         to player. The larger the value, the more favorable.
     */
    public int evaluate(boolean doAdvancedEval) {

        Four[] fours = BoardDef.FOURS;

        List<Integer> winningIndices = new ArrayList<Integer>();
        List<Integer> seizedFactors = new ArrayList<Integer>();

        List<Integer> losingIndices = new ArrayList<Integer>();
        List<Integer> tabooFactors = new ArrayList<Integer>();

        int result = 0;
        int valueForFour;

        for (int i = 0; i < fours.length; i++) {
            Four four = fours[i];
            valueForFour = four.evaluate(cellValues);

            // a winning value supersedes anything
            if (Four.isTerminalPosition(valueForFour))
                return valueForFour;

            if (doAdvancedEval) {
                // if the cell is already a winning cell (i.e. the player
                // would win if he fills it), then there it we count it
                // exactly once. In effect, what we count is the number
                // of winning cells, rather than number of threes in a row

                if (valueForFour == Four.VALUE_THREE_IN_A_ROW
                    && isDuplicate4thCell(four, seizedFactors, winningIndices))
                    continue;

                if (valueForFour == -Four.VALUE_THREE_IN_A_ROW
                    && isDuplicate4thCell(four, tabooFactors, losingIndices))
                    continue;
            }

            result += valueForFour;
        }

        // the more seized factors, the better. For instance, if the 4th cell
        // is 36 forbids the other player from 4, 6, and 9 whereas if it
        // is 25 forbids only the factor 5, so the former has more value
        // conversely, the fewer taboo factors the better
        if (doAdvancedEval) {
            result += seizedFactors.size() * Four.VALUE_FORBIDDEN_FACTOR;
            result -= tabooFactors.size() * Four.VALUE_FORBIDDEN_FACTOR;
        }

        return result;
    }

    public Player getWinnerIfAny(int product) {
        int[] foursOfLastFilledCell = BoardDef.getFourIndicesFor(product);
        for (int i = 0; i < foursOfLastFilledCell.length; i++) {
            Four four = BoardDef.FOURS[foursOfLastFilledCell[i]];
            Player valueIfAllSame
                = four.getValueIfAllSame(getCellValues());

            if (valueIfAllSame != null)
                return valueIfAllSame;
        }
        return null;
    }

    public int[] getWinningFourIfAny() {
        for (int i = 0; i < BoardDef.FOURS.length; i++) {
            Four four = BoardDef.FOURS[i];
            if (four.isTerminalPosition(cellValues))
                return four.getIndices();
        }
        return null;
    }

    public MulticGameCell getCellState(int product) {
        int cellIndex = BoardDef.getIndexForProduct(product);
        return new MulticGameCell(cellIndex, cellValues[cellIndex]);
    }

    public boolean isEmptyCell(int product) {
        int cellIndex = BoardDef.getIndexForProduct(product);
        return cellValues[cellIndex] == null;
    }

    //
    // XGameGridUiModel
    //

    public int getNumCells() {
        return getCellValues().length;
    }

    public String getCellContent(int index) {
        return String.valueOf(BoardDef.PRODUCTS[index]);
    }

    public Player getCellOccupantIfAny(int index) {
        return getCellValues()[index];
    }

    //
    // Mutators
    //

    /**
     * Fills the specified empty cell with the
     */
    public void fillCell(int product, Player value) {
        int cellIndex = BoardDef.getIndexForProduct(product);
        cellValues[cellIndex] = value;
    }

    public Player[] getCellValues() {
        return cellValues;
    }

    //
    // Private
    //

    /**
     * Returns true if the winning cell index is included in
     * the specified cell indices.
     */
    private boolean isDuplicate4thCell(Four fourWithThreeInRow,
                                       List<Integer> factors,
                                       List<Integer> cellIndices) {

        int cellIndex = fourWithThreeInRow.getFirstEmptyIndex(cellValues);

        if (cellIndices.contains(cellIndex))
            return true; // do not count the value of this four

        cellIndices.add(cellIndex);

        int[] factorsForCell = BoardDef.getFactorsForProductAtIndex(cellIndex);

        for (int f = 0; f < factorsForCell.length; f++)
            factors.add(factorsForCell[f]);

        return false;
    }
}
