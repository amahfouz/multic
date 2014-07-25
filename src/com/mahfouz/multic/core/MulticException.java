/*
 * CONFIDENTIAL
 * Copyright 2013 Webalo, Inc.  All rights reserved.
 */

package com.mahfouz.multic.core;

/**
 * Exception indicating invalid game move or state.
 */
public final class MulticException extends Exception {

    private final MulticGameCell cellIfAny;

    public MulticException(String detailMessage, MulticGameCell gameCell) {
        super(detailMessage);

        this.cellIfAny = gameCell;
    }

    public MulticGameCell getCellIfAny() {
        return this.cellIfAny;
    }
}
