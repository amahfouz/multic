/*
 * CONFIDENTIAL
 * Copyright 2013 Webalo, Inc.  All rights reserved.
 */

package com.mahfouz.multic.core;

/**
 * State of both knobs combined.
 */
public final class Knobs {

    private final Knob.Pos topKnobPos;
    private final Knob.Pos botKnobPos;

    private Knobs(Knob.Pos topKnobPos, Knob.Pos botKnobPos) {
        if (topKnobPos == null || botKnobPos == null)
            throw new IllegalArgumentException();

        this.topKnobPos = topKnobPos;
        this.botKnobPos = botKnobPos;
    }

    public static Knobs createInit(boolean randomFirstPos) {
        final int firstKnobPos;
        final int secondKnobPos;
        if (randomFirstPos) {
            firstKnobPos = (int)(Math.floor(Math.random() * 9) + 1);
            secondKnobPos = (int)(Math.floor(Math.random() * 9) + 1);
        }
        else {
            firstKnobPos = 1;
            secondKnobPos = 1;
        }
        return new Knobs(new Knob.Pos(firstKnobPos),
                         new Knob.Pos(secondKnobPos));
    }

    //
    // getters
    //

    public int getCurProduct() {
        return topKnobPos.get() * botKnobPos.get();
    }

    public int getPosFor(Knob.Location loc) {
        return getObjFor(loc).get();
    }

    public int getOtherPos(Knob.Location loc) {
        switch (loc) {
            case BOTTOM:
                return topKnobPos.get();

            case TOP:
                return botKnobPos.get();

            default:
                throw new IllegalArgumentException();
        }
    }

    public boolean areAtSamePos() {
        return topKnobPos.get() == botKnobPos.get();
    }

    public int getProdIfMoved(int pos, Knob.Location knobLoc) {
        return pos * getOtherPos(knobLoc);
    }

    public String toString() {
        return "Top = " + topKnobPos.get() + ", Bottom = " + botKnobPos.get();
    }

    //
    // mutators
    //

    public void set(Knob.Location loc, int pos) {
        getObjFor(loc).set(pos);
    }

    //
    // private
    //

    private Knob.Pos getObjFor(Knob.Location loc) {
        switch (loc) {
            case TOP:
                return topKnobPos;

            case BOTTOM:
                return botKnobPos;

            default:
                throw new IllegalArgumentException();
        }
    }
}
