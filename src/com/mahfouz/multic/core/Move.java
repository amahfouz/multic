package com.mahfouz.multic.core;

/**
 * Specifies a move made either by human or computer.
 */
public interface Move {

    int getPos();
    Knob.Location getKnobLoc();

    //
    // nested type
    //

    public static final class Immutable implements Move {

        private final int knobPos;
        private final Knob.Location knobLocation;

        public Immutable(int pos, Knob.Location knobLocation) {
            if (knobLocation == null)
                throw new IllegalArgumentException();

            if (! Knob.isValidPos(pos))
                throw new IllegalArgumentException();

            this.knobPos = pos;
            this.knobLocation = knobLocation;
        }

        public int getPos() {
            return this.knobPos;
        }

        public Knob.Location getKnobLoc() {
            return this.knobLocation;
        }

        public String toString() {
            return knobLocation.getLabel() + " knob to " + knobPos;
        }
    }

    public static final class Mutable implements Move {

        private int pos = -1;
        private Knob.Location knobLocation;

        public int getPos() {
            return this.pos;
        }

        public Knob.Location getKnobLoc() {
            return this.knobLocation;
        }

        public void set(Knob.Location loc, int newPos) {
            this.knobLocation = loc;
            this.pos = newPos;
        }

        public Immutable toImmutable() {
            return new Immutable(pos, knobLocation);
        }

        public void copyInto(Mutable other) {
            other.set(knobLocation, pos);
        }

        public String toString() {
            return knobLocation.getLabel() + " knob to " + pos;
        }
    }
}
