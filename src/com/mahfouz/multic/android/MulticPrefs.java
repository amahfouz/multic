/*
 * CONFIDENTIAL
 * Copyright 2014 Webalo, Inc.  All rights reserved.
 */

package com.mahfouz.multic.android;

import android.content.Context;
import android.content.SharedPreferences;

import com.mahfouz.multic.R;
import com.mahfouz.multic.core.Difficulty;
import com.mahfouz.multic.uim.XGameModel;

/**
 * Settings for the game.
 */
public final class MulticPrefs {

    //
    // getters
    //

    public static Difficulty getDefaultDifficulty() {
        return Difficulty.MEDIUM;
    }

    public static Difficulty getDifficulty() {
        Context ctx = MulticApplication.getInstance().getApplicationContext();
        SharedPreferences prefs = getSharedPrefs();
        int prefDifficultyOrdinal = prefs.getInt
            (ctx.getString(R.string.pref_name_difficulty),
             XGameModel.DEFAULT_DIFFICULTY.ordinal());

        if (prefDifficultyOrdinal >= Difficulty.values().length)
            prefDifficultyOrdinal = Difficulty.values().length - 1;

        return Difficulty.values()[prefDifficultyOrdinal];
    }

    public static boolean isRandomKnobStart() {
        Context ctx = MulticApplication.getInstance().getApplicationContext();
        return getSharedPrefs().getBoolean
            (ctx.getString(R.string.pref_name_random_start), false);
    }

    public static boolean humanStarts() {
        Context ctx = MulticApplication.getInstance().getApplicationContext();
        return getSharedPrefs().getBoolean
            (ctx.getString(R.string.pref_name_who_starts), true);
    }

    //
    // setters
    //

    public static void setDifficulty(int difficultyOrdinal) {
        Context ctx = MulticApplication.getInstance().getApplicationContext();
        SharedPreferences.Editor editor = getSharedPrefs().edit();
        editor.putInt(ctx.getString(R.string.pref_name_difficulty),difficultyOrdinal);
        editor.commit();
    }

    public static void setKnobStartPosRandom(boolean isRandom) {
        Context ctx = MulticApplication.getInstance().getApplicationContext();
        SharedPreferences.Editor editor = getSharedPrefs().edit();
        editor.putBoolean(ctx.getString(R.string.pref_name_random_start), isRandom);
        editor.commit();
    }

    public static void setHumanStarts(boolean isHuman) {
        Context ctx = MulticApplication.getInstance().getApplicationContext();
        SharedPreferences.Editor editor = getSharedPrefs().edit();
        editor.putBoolean(ctx.getString(R.string.pref_name_who_starts), isHuman);
        editor.commit();
    }

    //
    // private
    //

    private static SharedPreferences getSharedPrefs() {
        Context ctx = MulticApplication.getInstance().getApplicationContext();
        return ctx.getSharedPreferences
            (ctx.getString(R.string.shared_pref_name), 0);
    }
}
