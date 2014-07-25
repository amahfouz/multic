package com.mahfouz.multic.android;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.mahfouz.multic.core.Player;
import com.mahfouz.multic.uim.XGameGridUiModel;
import com.mahfouz.multic.R;
/**
 * Content provider for the game grid.
 */
public final class GridContentProvider extends BaseAdapter {

    public final int COLOR_BACKGROUND;
    public final int COLOR_EMPTY;
    public final int COLOR_HUMAN;
    public final int COLOR_COMPUTER;

    private final XGameGridUiModel.Provider uimProvider;
    private final TextView[] cellView;

    public GridContentProvider(Context context,
                               XGameGridUiModel.Provider uimProvider) {
        if (context == null || uimProvider == null)
            throw new IllegalArgumentException();

        this.COLOR_BACKGROUND
            = context.getResources().getColor(R.color.grid_background);
        this.COLOR_COMPUTER
            = context.getResources().getColor(R.color.grid_cell_computer);
        this.COLOR_HUMAN
            = context.getResources().getColor(R.color.grid_cell_human);
        this.COLOR_EMPTY
            = context.getResources().getColor(R.color.grid_cell_empty);

        this.uimProvider = uimProvider;

        this.cellView = new TextView[getUim().getNumCells()];

        TextView textView;
        for (int i = 0; i < cellView.length; i++) {
            GradientDrawable bkgnd = new GradientDrawable();
            bkgnd.setCornerRadius(4);

            textView = new TextView(context);
            textView.setText(getUim().getCellContent(i));
            textView.setTypeface(null, Typeface.BOLD);
            textView.setTextSize(20);
            textView.setLayoutParams(new GridView.LayoutParams(80, 80));
            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundDrawable(bkgnd);
            cellView[i] = textView;
            updateBackgroundColorForCell(i, false);
        }
    }

    //
    // BaseAdapter implementation
    //

    @Override
    public int getCount() {
        return getUim().getNumCells();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return cellView[i];
    }

    //
    // package
    //

    public void updateCell(int cellIndex) {
        updateBackgroundColorForCell(cellIndex, true);
    }

    public void updateAll() {
        for (int i = 0; i < cellView.length; i++)
            updateBackgroundColorForCell(i, false);
    }

    //
    // private
    //

    private XGameGridUiModel getUim() {
        return uimProvider.get();
    }

    private void updateBackgroundColorForCell(int index, boolean animate) {
        Player p = getUim().getCellOccupantIfAny(index);
        int color = (p == Player.COMPUTER)
            ? COLOR_COMPUTER
            : (p == Player.HUMAN)
                ? COLOR_HUMAN
                : COLOR_EMPTY;

        TextView textView = cellView[index];

        if (animate)
            animateBacgkroundColorChange(textView, color);
        else {
            ((GradientDrawable)textView.getBackground()).setColor(color);
            textView.invalidate();
        }
    }

    private void animateBacgkroundColorChange(final View textView, int toColor) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject
            (new ArgbEvaluator(), COLOR_EMPTY, toColor);

        colorAnimation.setDuration(760);

        colorAnimation.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                ((GradientDrawable)textView.getBackground()).setColor
                    (((Integer)animator.getAnimatedValue()));
            }
        });
        colorAnimation.start();
    }
}
