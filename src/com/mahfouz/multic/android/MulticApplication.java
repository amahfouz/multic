package com.mahfouz.multic.android;

import com.mahfouz.multic.uim.XGameModel;

import android.app.Application;

/**
 * Global application.
 *
 * Holds global game state to be reused on main activity recreation.
 */
public final class MulticApplication extends Application {

    private static MulticApplication instance;
    private XGameModel gameIfAny;

    //
    // static
    //

    public static Application getInstance() {
        return instance;
    }

    //
    // overriden
    //

    @Override
    public void onCreate() {
        MulticApplication.instance = this;
    }

    //
    // public
    //

    public void storeGameModel(XGameModel gameModel) {
        this.gameIfAny = gameModel;
    }

    public XGameModel getGameModelIfAny() {
        return gameIfAny;
    }
}
