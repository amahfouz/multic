package com.mahfouz.multic.uim;

import com.mahfouz.multic.core.Player;

/**
 * UI model for the game grid specifying cell content and color.
 */
public interface XGameGridUiModel {

    int getNumCells();
    String getCellContent(int index);
    Player getCellOccupantIfAny(int index);

    interface Provider {
        XGameGridUiModel get();
    }

    final class Util {
        public static boolean isEmpty(XGameGridUiModel grid) {
            int numCells = grid.getNumCells();
            for (int i = 0; i < numCells; i++) {
                if (grid.getCellOccupantIfAny(i) != null)
                    return false;
            }
            return true;
        }
    }
}
