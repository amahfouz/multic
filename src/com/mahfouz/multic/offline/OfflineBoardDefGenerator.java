package com.mahfouz.multic.offline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Generates metadata to populate BoardDef.
 */
public final class OfflineBoardDefGenerator {

    private static final int MAX_LABEL = 81;
    private static final int GRID_W = 6;
    private static final int GRID_H = 6;
    private static final int NUM_CELLS = GRID_H * GRID_W;

    private static final int[][] GRID
        = new int[][] {{  1,   2,   3,   4,   5,   6 },
                       {  7,   8,   9,  10,  12,  14 },
                       { 15,  16,  18,  20,  21,  24 },
                       { 25,  27,  28,  30,  32,  35 },
                       { 36,  40,  42,  45,  48,  49 },
                       { 54,  56,  63,  64,  72,  81 }};


    /**
     * Generates a list of fours, identified by product (not index).
     */
    public static final List<int[]> generateFours() {

        ArrayList<int[]> result = new ArrayList<int[]>();

        int[] prods;
        int[][] board = GRID;

        // find all horizontal fours

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length - 3; j++) {
                prods = new int[4];
                result.add(prods);
                for (int k = j; k < j + 4; k++)
                    prods[k - j] = board[i][k];
            }
        }

        // find all vertical fours
        for (int j = 0; j < board[0].length; j++) {
            for (int i = 0; i < board.length - 3; i++) {
                prods = new int[4];
                result.add(prods);
                for (int k = i; k < i + 4; k++)
                    prods[k - i] = board[k][j];
            }
        }

        // find all forward diagonal fours
        // all forward fours originate in the 3x3 top left corner
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                prods = new int[4];
                result.add(prods);
                for (int k = 0; k < 4; k++)
                    prods[k] = board[i + k][j + k];
            }
        }

        // find all backward fours
        // all backward fours originate in the 3x3 top right corner
        for (int i = 0; i < 3; i++) {
            for (int j = 3; j < 6; j++) {
                prods = new int[4];
                result.add(prods);
                for (int k = 0; k < 4; k++)
                    prods[k] = board[i + k][j - k];
            }
        }

        return result;
    }

    private static int[] genIndicesAndFlattenGrid() {
        final int[] PRODUCTS = new int[NUM_CELLS];
        final int[] indexForProduct = new int[MAX_LABEL + 1];

        Arrays.fill(indexForProduct, -1);

        int index = 0;
        for (int row = 0; row < GRID.length; row++) {
            for (int col = 0; col < GRID[0].length; col++) {
                int product = GRID[row][col];
                indexForProduct[product] = index;
                PRODUCTS[index++] = product;
            }
        }

        // print all products

        StringBuffer prodsAsStr = new StringBuffer();
        for (int i = 0; i < PRODUCTS.length; i++) {
            prodsAsStr.append(PRODUCTS[i]);
            if (i < PRODUCTS.length - 1)
                prodsAsStr.append(", ");
        }

        System.out.println
            ("private static final int[] PRODUCTS = new int[] { "
             + prodsAsStr + " }");

        // print all indices

        StringBuffer indicesStr = new StringBuffer();
        for (int i = 0; i < indexForProduct.length; i++) {
            indicesStr.append(indexForProduct[i]);
            if (i < indexForProduct.length - 1)
                indicesStr.append(", ");
        }

        System.out.print
            ("private static final int[] INDEX_FOR_PRODUCT = new int[] { "
             + indicesStr + " }");

        return indexForProduct;
    }

    private static String appendAsCsString(int [] array) {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            b.append(array[i]);
            if (i < array.length - 1)
                b.append(", ");
        }
        return b.toString();
    }

    private static int[][] computeFoursIndicesForProds(List<int[]> foursAsProds) {
        final List<Integer>[] perProduct = new List[MAX_LABEL + 1];
        for (int i = 0; i < perProduct.length; i++)
            perProduct[i] = new ArrayList<Integer>();

        for (int i = 0; i < foursAsProds.size(); i++) {
            int[] curFour = foursAsProds.get(i);
            for (int j = 0; j < curFour.length; j++) {
                int prod = curFour[j];
                perProduct[prod].add(i);
            }
        }

        int [][] result = new int[perProduct.length][];
        for (int i = 0; i < result.length; i++) {
            List<Integer> list = perProduct[i];
            if (! list.isEmpty()) {
                result[i] = new int[list.size()];
                for (int j = 0; j < result[i].length; j++) {
                    result[i][j] = list.get(j);
                }
            }
        }
        return result;
    }

    /**
     * Factorizes each of the products to its factors.
     */
    private static int[][] factorize(int[] indexForProds) {
        List<Integer>[] factors = new List[NUM_CELLS];
        for (int f = 0; f < factors.length; f++)
            factors[f] = new ArrayList<Integer>();

        for (int knob1 = 1; knob1 <= 9; knob1++) {
            for (int knob2 = 1; knob2 <= 9; knob2++) {
                int prod = knob1 * knob2;
                int index = indexForProds[prod];

                List factorsForProd = factors[index];

                if (! factorsForProd.contains(knob1))
                    factorsForProd.add(knob1);

                if (! factorsForProd.contains(knob2))
                    factorsForProd.add(knob2);
            }
        }

        int[][] result = new int[NUM_CELLS][];
        for (int i = 0; i < result.length; i++) {
            Collections.sort(factors[i]);
            result[i] = new int[factors[i].size()];
            for (int j = 0; j < result[i].length; j++) {
                result[i][j] = factors[i].get(j);
            }
        }
        return result;
    }

    //
    // main
    //

    public static void main(String[] args) {
        int[] indexForProd = genIndicesAndFlattenGrid();
        List<int[]> foursAsProds = generateFours();

        int numFours = foursAsProds.size();
        System.out.println("Num fours " + numFours);

        // convert fours of product to fours of indices

        StringBuffer fourAsStr = new StringBuffer();
        for (int i = 0; i < numFours; i++) {
            int[] prods = foursAsProds.get(i);
            int[] indices = new int[4];

            for (int j = 0; j < indices.length; j++) {
                indices[j] = indexForProd[prods[j]];
            }

            fourAsStr.append("new Four(new int[] {")
                     .append(appendAsCsString(indices))
                     .append("}), \n");
        }

        System.out.println(fourAsStr.toString());

        int[][] foursIndicesForProd = computeFoursIndicesForProds(foursAsProds);

        System.out.println("*** Printing map from prod to fours");

        StringBuffer b = new StringBuffer();
        for (int i = 0; i < foursIndicesForProd.length; i++) {
            int[] cur = foursIndicesForProd[i];
            if (cur == null)
                b.append("null, \n");
            else
                b.append("new int[] {")
                 .append(appendAsCsString(cur))
                 .append("}, \n");
        }

        System.out.println(b.toString());

        System.out.println("*** Find factors for each product");

        StringBuffer factorsStr = new StringBuffer();
        int[][] factors = factorize(indexForProd);
        for (int i = 0; i < factors.length; i++) {
            factorsStr
                .append("new int[] {")
                .append(appendAsCsString(factors[i]))
                .append("}, \n");
        }

        System.out.println(factorsStr.toString());
    }
}
