/*
 * CONFIDENTIAL
 * Copyright 2013 Webalo, Inc.  All rights reserved.
 */

package com.mahfouz.multic.core;

/**
 * Definition of board cells and all combinations of fours in a row.
 */
public final class BoardDef {

    public static final int GRID_DIM = 6;
    public static final int NUM_CELLS = GRID_DIM * GRID_DIM;

    // values corresponding to board cells
    public static final int[] PRODUCTS = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 14, 15, 16, 18, 20, 21, 24, 25, 27, 28, 30, 32, 35, 36, 40, 42, 45, 48, 49, 54, 56, 63, 64, 72, 81 };

    // all possible combinations of four-in-a-row
    // values in these arrays are indices into an
    // array that flattens the grid
    public static final Four[] FOURS = new Four[]
       {new Four(new int[] {0, 1, 2, 3}),
        new Four(new int[] {1, 2, 3, 4}),
        new Four(new int[] {2, 3, 4, 5}),
        new Four(new int[] {6, 7, 8, 9}),
        new Four(new int[] {7, 8, 9, 10}),
        new Four(new int[] {8, 9, 10, 11}),
        new Four(new int[] {12, 13, 14, 15}),
        new Four(new int[] {13, 14, 15, 16}),
        new Four(new int[] {14, 15, 16, 17}),
        new Four(new int[] {18, 19, 20, 21}),
        new Four(new int[] {19, 20, 21, 22}),
        new Four(new int[] {20, 21, 22, 23}),
        new Four(new int[] {24, 25, 26, 27}),
        new Four(new int[] {25, 26, 27, 28}),
        new Four(new int[] {26, 27, 28, 29}),
        new Four(new int[] {30, 31, 32, 33}),
        new Four(new int[] {31, 32, 33, 34}),
        new Four(new int[] {32, 33, 34, 35}),
        new Four(new int[] {0, 6, 12, 18}),
        new Four(new int[] {6, 12, 18, 24}),
        new Four(new int[] {12, 18, 24, 30}),
        new Four(new int[] {1, 7, 13, 19}),
        new Four(new int[] {7, 13, 19, 25}),
        new Four(new int[] {13, 19, 25, 31}),
        new Four(new int[] {2, 8, 14, 20}),
        new Four(new int[] {8, 14, 20, 26}),
        new Four(new int[] {14, 20, 26, 32}),
        new Four(new int[] {3, 9, 15, 21}),
        new Four(new int[] {9, 15, 21, 27}),
        new Four(new int[] {15, 21, 27, 33}),
        new Four(new int[] {4, 10, 16, 22}),
        new Four(new int[] {10, 16, 22, 28}),
        new Four(new int[] {16, 22, 28, 34}),
        new Four(new int[] {5, 11, 17, 23}),
        new Four(new int[] {11, 17, 23, 29}),
        new Four(new int[] {17, 23, 29, 35}),
        new Four(new int[] {0, 7, 14, 21}),
        new Four(new int[] {1, 8, 15, 22}),
        new Four(new int[] {2, 9, 16, 23}),
        new Four(new int[] {6, 13, 20, 27}),
        new Four(new int[] {7, 14, 21, 28}),
        new Four(new int[] {8, 15, 22, 29}),
        new Four(new int[] {12, 19, 26, 33}),
        new Four(new int[] {13, 20, 27, 34}),
        new Four(new int[] {14, 21, 28, 35}),
        new Four(new int[] {3, 8, 13, 18}),
        new Four(new int[] {4, 9, 14, 19}),
        new Four(new int[] {5, 10, 15, 20}),
        new Four(new int[] {9, 14, 19, 24}),
        new Four(new int[] {10, 15, 20, 25}),
        new Four(new int[] {11, 16, 21, 26}),
        new Four(new int[] {15, 20, 25, 30}),
        new Four(new int[] {16, 21, 26, 31}),
        new Four(new int[] {17, 22, 27, 32})};

    // entry at index 'prod' in this array holds the index of
    // the entry whose value is 'prod' in the PRODUCTS array
    private static final int[] INDEX_FOR_PRODUCT
        = new int[] { -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -1, 10, -1, 11, 12, 13, -1, 14, -1, 15, 16, -1, -1, 17, 18, -1, 19, 20, -1, 21, -1, 22, -1, -1, 23, 24, -1, -1, -1, 25, -1, 26, -1, -1, 27, -1, -1, 28, 29, -1, -1, -1, -1, 30, -1, 31, -1, -1, -1, -1, -1, -1, 32, 33, -1, -1, -1, -1, -1, -1, -1, 34, -1, -1, -1, -1, -1, -1, -1, -1, 35 };

    private static final int[][] FOUR_INDICES_FOR_PRODUCT = new int[][] {
        null,
        new int[] {0, 18, 36},
        new int[] {0, 1, 21, 37},
        new int[] {0, 1, 2, 24, 38},
        new int[] {0, 1, 2, 27, 45},
        new int[] {1, 2, 30, 46},
        new int[] {2, 33, 47},
        new int[] {3, 18, 19, 39},
        new int[] {3, 4, 21, 22, 36, 40},
        new int[] {3, 4, 5, 24, 25, 37, 41, 45},
        new int[] {3, 4, 5, 27, 28, 38, 46, 48},
        null,
        new int[] {4, 5, 30, 31, 47, 49},
        null,
        new int[] {5, 33, 34, 50},
        new int[] {6, 18, 19, 20, 42},
        new int[] {6, 7, 21, 22, 23, 39, 43, 45},
        null,
        new int[] {6, 7, 8, 24, 25, 26, 36, 40, 44, 46, 48},
        null,
        new int[] {6, 7, 8, 27, 28, 29, 37, 41, 47, 49, 51},
        new int[] {7, 8, 30, 31, 32, 38, 50, 52},
        null,
        null,
        new int[] {8, 33, 34, 35, 53},
        new int[] {9, 18, 19, 20, 45},
        null,
        new int[] {9, 10, 21, 22, 23, 42, 46, 48},
        new int[] {9, 10, 11, 24, 25, 26, 39, 43, 47, 49, 51},
        null,
        new int[] {9, 10, 11, 27, 28, 29, 36, 40, 44, 50, 52},
        null,
        new int[] {10, 11, 30, 31, 32, 37, 41, 53},
        null,
        null,
        new int[] {11, 33, 34, 35, 38},
        new int[] {12, 19, 20, 48},
        null,
        null,
        null,
        new int[] {12, 13, 22, 23, 49, 51},
        null,
        new int[] {12, 13, 14, 25, 26, 42, 50, 52},
        null,
        null,
        new int[] {12, 13, 14, 28, 29, 39, 43, 53},
        null,
        null,
        new int[] {13, 14, 31, 32, 40, 44},
        new int[] {14, 34, 35, 41},
        null,
        null,
        null,
        null,
        new int[] {15, 20, 51},
        null,
        new int[] {15, 16, 23, 52},
        null,
        null,
        null,
        null,
        null,
        null,
        new int[] {15, 16, 17, 26, 53},
        new int[] {15, 16, 17, 29, 42},
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        new int[] {16, 17, 32, 43},
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        new int[] {17, 35, 44}
    };

    // Factors for each product
    //
    // entry at index
    //           INDEX_FOR_PRODUCT[prod]
    // in this array holds the factors for the number 'prod'
    private static final int[][] FACTORS_FOR_PRODUCT = new int[][] {
        new int[] {1},
        new int[] {1, 2},
        new int[] {1, 3},
        new int[] {1, 2, 4},
        new int[] {1, 5},
        new int[] {1, 2, 3, 6},
        new int[] {1, 7},
        new int[] {1, 2, 4, 8},
        new int[] {1, 3, 9},
        new int[] {2, 5},
        new int[] {2, 3, 4, 6},
        new int[] {2, 7},
        new int[] {3, 5},
        new int[] {2, 4, 8},
        new int[] {2, 3, 6, 9},
        new int[] {4, 5},
        new int[] {3, 7},
        new int[] {3, 4, 6, 8},
        new int[] {5},
        new int[] {3, 9},
        new int[] {4, 7},
        new int[] {5, 6},
        new int[] {4, 8},
        new int[] {5, 7},
        new int[] {4, 6, 9},
        new int[] {5, 8},
        new int[] {6, 7},
        new int[] {5, 9},
        new int[] {6, 8},
        new int[] {7},
        new int[] {6, 9},
        new int[] {7, 8},
        new int[] {7, 9},
        new int[] {8},
        new int[] {8, 9},
        new int[] {9},
    };

    //
    // public methods
    //

    public static int getIndexForProduct(int product) {
        int cellIndex = INDEX_FOR_PRODUCT[product];
        if (cellIndex < 0)
            throw new IllegalArgumentException("Invalid product " + product);
        return cellIndex;
    }

    /**
     * Return all fours in which the cell with the specified
     * product participates.
     */
    public static int[] getFourIndicesFor(int product) {
        return FOUR_INDICES_FOR_PRODUCT[product];
    }

    public static int[] getFactorsForProductAtIndex(int productIndex) {
        return FACTORS_FOR_PRODUCT[productIndex];
    }
}
