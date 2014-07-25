package com.mahfouz.multic.uim;

import com.mahfouz.multic.core.Knob;
import com.mahfouz.multic.core.Player;

/**
 * UI of the game.
 */
public interface XGameUi {

    void setWheelEnabled(Knob.Location loc, boolean isEnabled);
    void setWheelSelection(Knob.Location loc, int index, boolean animate);
    void showMessage(String message, Player player);
    void flashCells(int[] cellIndices, boolean isError, CompletionListener listener);
    void updateCell(int cellIndex, CompletionListener listener);
    void refreshGrid();
    void showPlayAgainControl();
}
