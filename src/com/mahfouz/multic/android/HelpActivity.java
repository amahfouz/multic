/*
 * CONFIDENTIAL
 * Copyright 2014 Webalo, Inc.  All rights reserved.
 */

package com.mahfouz.multic.android;

import com.mahfouz.multic.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.webkit.WebView;

/**
 * Activity showing HTML help.
 */
public final class HelpActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_help);
        // Show the "Up" button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);

        WebView webView = (WebView)findViewById(R.id.helpView);

        webView.loadUrl("file:///android_asset/multic-help.html");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
