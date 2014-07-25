
package com.mahfouz.multic.android;

import com.mahfouz.multic.R;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.support.v4.app.NavUtils;

/**
 * Activity showing app settings.
 */
public final class SettingsActivity
    extends Activity
    implements RadioGroup.OnCheckedChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // Show the Up button in the action bar.
        setupActionBar();

        RadioGroup levelRadios
            = (RadioGroup)findViewById(R.id.difficultyRadioGroup);

        int curDifficulty = MulticPrefs.getDifficulty().ordinal();

        levelRadios.check(getRadioIdForLevel(curDifficulty));
        levelRadios.setOnCheckedChangeListener(this);

        RadioGroup startPosRadio
            = (RadioGroup)findViewById(R.id.startPosRadioGroup);

        boolean isRandom = MulticPrefs.isRandomKnobStart();

        startPosRadio.check(isRandom
                                ? R.id.startPosRandomRadio
                                : R.id.startPosFixedRadio);
        startPosRadio.setOnCheckedChangeListener(this);

        RadioGroup whoStartRadio
            = (RadioGroup)findViewById(R.id.whoStartsRadioGroup);

        boolean humanStarts = MulticPrefs.humanStarts();

        whoStartRadio.check(humanStarts
                                ? R.id.humanStartsRadio
                                : R.id.computerStartsRadio);

        whoStartRadio.setOnCheckedChangeListener(this);
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

    //
    // RadioGroup.OnCheckedChangeListener implementation
    //

    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        if (radioGroup.getId() == R.id.difficultyRadioGroup) {
            View radio = findViewById(checkedId);
            int difficulty = Integer.parseInt(radio.getTag().toString());
            MulticPrefs.setDifficulty(difficulty);
        }
        else if (radioGroup.getId() == R.id.startPosRadioGroup) {
            boolean isRandom = (checkedId == R.id.startPosRandomRadio);
            MulticPrefs.setKnobStartPosRandom(isRandom);
        }
        else if (radioGroup.getId() == R.id.whoStartsRadioGroup) {
            boolean isHuman = (checkedId == R.id.humanStartsRadio);
            MulticPrefs.setHumanStarts(isHuman);
        }
    }

    //
    // private methods
    //

    private int getRadioIdForLevel(int curDifficulty) {
        RadioGroup radioGroup
            = (RadioGroup)findViewById(R.id.difficultyRadioGroup);

        int numRadios = radioGroup.getChildCount();
        for (int i = 0; i < numRadios; i++) {
            RadioButton radio = (RadioButton)radioGroup.getChildAt(i);
            if (Integer.parseInt(radio.getTag().toString()) == curDifficulty)
                return radio.getId();
        }

        Log.w("SETTINGS", "Invalid difficulty setting. Using default.");

        return R.id.radio1;
    }

    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
