package com.mahfouz.multic.core;

/**
 * State of a knob
 */
public final class Knob {

    private static final int MIN_FACTOR = 1;
    public static final int MAX_FACTOR = 9;

//    public static final int[] FACTORS = new int[MAX_FACTOR - MIN_FACTOR + 1];
//
//    static {
//        for (int i = 0; i < FACTORS.length; i++)
//            FACTORS[i] = i + 1;
//    }

    public static boolean isValidPos(int pos) {
        return pos >= MIN_FACTOR && pos <= MAX_FACTOR;
    }

    public static int factorForPos(int pos) {
        // 'pos' is zero based while factors start with '1'
        return pos + 1;
    }

    public static int posForFactor(int factor) {
        return factor - 1;
    }

    //
    // nested types
    //

    public static enum Location {
        TOP("top") ,
        BOTTOM("bottom");

        private final String label;

        Location(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    };

    public static final class Pos {

        private int pos;

        public Pos(int pos) {
            if (! isValidPos(pos))
                throw new IllegalArgumentException();
            this.pos = pos;
        }

        public int get() {
            return pos;
        }

        public void set(int newPos) {
            if (! isValidPos(newPos))
                throw new IllegalArgumentException
                    ("Invalid position (" + newPos + ").");

            if (newPos == pos)
                throw new IllegalArgumentException
                    ("New position same as current.");

            this.pos = newPos;
        }
    }
}
