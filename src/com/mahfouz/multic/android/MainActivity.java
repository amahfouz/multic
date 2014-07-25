package com.mahfouz.multic.android;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mahfouz.multic.R;
import com.mahfouz.multic.core.Difficulty;
import com.mahfouz.multic.core.Knob;
import com.mahfouz.multic.core.Player;
import com.mahfouz.multic.uim.CompletionListener;
import com.mahfouz.multic.uim.XGameGridUiModel;
import com.mahfouz.multic.uim.XGameModel;
import com.mahfouz.multic.uim.XGameUi;
import com.mahfouz.multic.util.MulticLog;

/**
 * Main activity for the application.
 */
public final class MainActivity extends Activity {

    private static final String LOG_TAG = "mul-tic-tac-toe";
    private static final String PLAY_AGAIN_MESSAGE = "Tap grid to play again";

    private XGameModel game;

    private SquareGridView grid;
    private WheelView firstKnob;
    private WheelView secondKnob;
    private TextView messageView;
    private Toast curToast;

    private boolean hasAdjustedLayout;

    //
    // Activity implementation
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MulticApplication app = (MulticApplication)getApplication();

        Object savedGameState = getLastNonConfigurationInstance();
        this.game = (savedGameState instanceof XGameModel)
            ? (XGameModel)savedGameState
            : (app.getGameModelIfAny() != null)
                ? app.getGameModelIfAny()
                : createNewGame();

        // store game in app to restore on activity recreation
        app.storeGameModel(game);

        setContentView(R.layout.activity_main);

        this.grid = (SquareGridView)findViewById(R.id.gameBoardView);

        AdapterView.OnItemClickListener gridClickListener
            = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // if game is over start new game
                if (! game.isGameInProgress())
                    startNewGame();
            }
        };

        grid.init(new XGameGridUiModel.Provider() {
            public XGameGridUiModel get() {
                return game.getGridUiModel();
            }
        }, gridClickListener);

        this.firstKnob = setupSpinner(R.id.firstKnob);
        this.secondKnob = setupSpinner(R.id.secondKnob);
        this.messageView = (TextView)findViewById(R.id.messageView);

        GradientDrawable bkgnd = new GradientDrawable();
        bkgnd.setCornerRadius(10);
        messageView.setBackgroundDrawable(bkgnd);

        SharedPreferences sp = getSharedPrefs();
        boolean isFirstTime = sp.getBoolean("first-time", true);
        if (isFirstTime) {
            sp.edit().putBoolean("first-time", false).commit();
            startActivity(new Intent(this, HelpActivity.class));
        }

        grid.getViewTreeObserver().addOnGlobalLayoutListener(new GlobalLayoutListener());

        game.registerUi(createThreadSafeUiCallback());
    }

    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first

        // Check if difficulty level has changed

        Difficulty prefDifficulty = MulticPrefs.getDifficulty();
        boolean difficultyChanged = prefDifficulty != game.getDifficulty();
        boolean computerShouldStart
            =  (! MulticPrefs.humanStarts())
            && XGameGridUiModel.Util.isEmpty(game.getGridUiModel());

        if (difficultyChanged || computerShouldStart)
            startNewGame();
    }

    @Override
    protected void onPause() {
        hideCurToast();
        super.onPause();
    }

    //
    // public methods
    //

    @Override
    public Object onRetainNonConfigurationInstance() {
        // retain game state on orientation change
        return game;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            case R.id.action_help:
                startActivity(new Intent(this, HelpActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * OnClick handler for the "New Game" button.
     */
    public void startNewGame(View view) {
        startNewGame();
    }

    //
    // private
    //

    private void hideCurToast() {
        if (curToast != null) {
            curToast.cancel();
            curToast = null;
        }
    }

    private void startNewGame() {
        showToast("Starting new game", false);

        this.game = createNewGame();

        game.registerUi(createThreadSafeUiCallback());
    }

    private XGameModel createNewGame() {
        MulticLog log = (getApplication().getApplicationInfo().flags
                         & ApplicationInfo.FLAG_DEBUGGABLE) == 0
            ? null
            : new MulticLog() {
                public void warn(String message) {
                    Log.w(LOG_TAG, message);
                }

                public void info(String message) {
                    Log.i(LOG_TAG, message);
                }

                public void debug(String message) {
                    Log.d(LOG_TAG, message);
                }
            };

        return new XGameModel(MulticPrefs.getDifficulty(),
                              MulticPrefs.isRandomKnobStart(),
                              MulticPrefs.humanStarts(),
                              log);
    }

    private XGameUi createThreadSafeUiCallback() {
        InvocationHandler uiCallbackHandler = new UiCallbackHandler();
        return (XGameUi)Proxy.newProxyInstance
            (getClassLoader(), new Class[] {XGameUi.class}, uiCallbackHandler);
    }

    private SharedPreferences getSharedPrefs() {
        return getSharedPreferences
            (getString(R.string.shared_pref_name), MODE_PRIVATE);
    }

    private WheelView setupSpinner(int viewId) {
        final WheelView knob = (WheelView) findViewById(viewId);
        NumericWheelAdapter wheelAdapter
            = new NumericWheelAdapter(this, 1, Knob.MAX_FACTOR);
        wheelAdapter.setItemResource(R.layout.wheel_item);
        wheelAdapter.setItemTextResource(R.id.wheel_item);
        knob.setViewAdapter(wheelAdapter);
        knob.setCyclic(true);
        knob.addScrollingListener(new WheelScrollListener());

        return knob;
    }

    private void showToast(String message, boolean isLong) {
        hideCurToast();

        this.curToast = Toast.makeText
            (MainActivity.this, message,
             isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        curToast.setGravity
            (Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
        curToast.show();
    }

    //
    // nested
    //

    private final class WheelScrollListener implements OnWheelScrollListener {

        private boolean scrollInProgress = false;
        /**
         * Ensure that user is only allowed to scroll on wheel at a time.
         *
         * Disable one wheel as soon as the other is touched and re-enables
         * when done.
         */
        public void onScrollingStarted(WheelView wheel) {
            if (! scrollInProgress) {
                scrollInProgress = true;
                setOtherWheelEnabled(wheel, false);
            }
        }

        public void onScrollingFinished(final WheelView wheel) {
            Log.i(LOG_TAG, "Wheel Scroll Finished " + wheel.getCurrentItem());

            scrollInProgress = false;
            // re-enable wheel that got disabled when scrolling started
            setOtherWheelEnabled(wheel, true);

            int viewId = wheel.getId();

            final Knob.Location knobLoc = (viewId == R.id.firstKnob)
                ? Knob.Location.TOP
                : (viewId == R.id.secondKnob)
                    ? Knob.Location.BOTTOM
                    : null;

            if (knobLoc == null) {
                Log.w(LOG_TAG, "Invalid view ID: " + viewId);
                return;
            }

            game.handleKnobChanged(knobLoc, wheel.getCurrentItem());
        }

        private void setOtherWheelEnabled(WheelView wheel, boolean enabled) {
            WheelView otherWheel = (wheel.getId() == R.id.firstKnob)
                ? secondKnob
                : firstKnob;

            otherWheel.setEnabled(enabled);
        }
    }

    private final class GlobalLayoutListener
        implements ViewTreeObserver.OnGlobalLayoutListener {

        public void onGlobalLayout() {
            // size of parent view is known when this method is called

            if (hasAdjustedLayout)
                return;

            // ensure this path is taken only once

            hasAdjustedLayout = true;

            LinearLayout root = (LinearLayout)findViewById(R.id.rootView);
            LinearLayout horz = (LinearLayout)findViewById(R.id.horizLayout);

            int gridSize = grid.adjustSize(root);

            if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT) {

                int totalConsumedHeight
                    = root.getPaddingTop()
                    + messageView.getHeight()
                    + horz.getPaddingTop() + horz.getPaddingBottom()
                    + gridSize
                    + root.getPaddingBottom();

                int wheelHeight = root.getHeight() - totalConsumedHeight;

                firstKnob.getLayoutParams().height = wheelHeight;
                secondKnob.getLayoutParams().height = wheelHeight;
            }
            else {
                // landscape
                firstKnob.getLayoutParams().height = gridSize * 6 / 10;
                secondKnob.getLayoutParams().height = gridSize * 6 / 10;
            }
        }
    }

    /**
     * Non-thread-safe implementation of XGameUi.
     */
    private final class SingleThreadedUiImpl implements XGameUi {

        public void setWheelEnabled(Knob.Location loc, boolean isEnabled) {
            if (loc == null)
                return;

            WheelView wheel = getWheelForLocation(loc);
            wheel.setEnabled(isEnabled);
            wheel.setFocusable(isEnabled);
            wheel.setFocusableInTouchMode(isEnabled);
        }

        public void setWheelSelection(Knob.Location loc, int index, boolean animate) {
            if (loc == null)
                return;

            getWheelForLocation(loc).setCurrentItem(index, animate);
        }

        public void showMessage(String message, Player player) {
            messageView.setText(message);

            int color = getResources().getColor
                (player == Player.HUMAN
                     ? R.color.grid_cell_human
                     : R.color.grid_cell_computer);

            ((GradientDrawable) messageView.getBackground()).setColor(color);
        }

        public void flashCells(final int[] cellIndices,
                               final boolean isError,
                               final CompletionListener listener) {

            AnimatorListenerAdapter flashListener
                = new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        listener.done();
                    }
                };

            grid.flashCells(cellIndices, isError, flashListener);
        }

        public void updateCell(int cellIndexIfAny,
                               final CompletionListener completion) {

            AnimatorListener listener = new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    Log.d(LOG_TAG, "Animation ended.");
                    completion.done();
                }
            };

            Log.d(LOG_TAG, "Updating cell with index " + cellIndexIfAny);
            if (cellIndexIfAny >= 0) {
                grid.updateCell(cellIndexIfAny, listener);
            }
        }

        public void showPlayAgainControl() {
            showToast(PLAY_AGAIN_MESSAGE, true);
        }

        public void refreshGrid() {
            grid.refresh();
        }

        private WheelView getWheelForLocation(Knob.Location knobLoc) {
            final WheelView view = (knobLoc == Knob.Location.TOP)
                ? firstKnob
                : (knobLoc == Knob.Location.BOTTOM)
                    ? secondKnob
                    : null;

            return view;
        }
    }

    private final class UiCallbackHandler implements InvocationHandler {

        private final SingleThreadedUiImpl delegate = new SingleThreadedUiImpl();

        public Object invoke(final Object proxy,
                             final Method method,
                             final Object[] args) {
            runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        method.invoke(delegate, args);
                    }
                    catch (Exception ex) {
                        Log.w(LOG_TAG, ex);
                    }
                }
            });

            // all methods of the GameUi interface return void
            return null;
        }
    }
}
