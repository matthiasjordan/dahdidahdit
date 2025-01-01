/****************************************************************************
    Dahdidahdit - an Android Morse trainer
    Copyright (C) 2021-2025 Matthias Jordan <matthias@paddlesandbugs.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
****************************************************************************/

package com.paddlesandbugs.dahdidahdit.base;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.Utils;

/**
 * Creates a tooltip.
 */
public class Tooltip {


    private static final String PREFS_PREFIX = "tooltip_iff_";
    private static boolean showAllTooltips = false;

    private final Context context;
    private String text;
    private int textSizeSp = 20;
    private int padding = 10;
    private int backgroundColor;
    private int gravity = Gravity.CENTER;
    private View anchor;
    private boolean below;
    private Tooltip after;
    private final int arrowHeight = 10;

    private String ifPrefsKey;


    public Tooltip(Context context) {
        this.context = context;
        backgroundColor(R.color.theme_secondary_darker);
    }


    public static void ignoreIff() {
        showAllTooltips = true;
    }


    public Tooltip below(View anchor) {
        this.anchor = anchor;
        this.below = true;
        return this;
    }


    public Tooltip above(View anchor) {
        this.anchor = anchor;
        this.below = false;
        return this;
    }


    public Tooltip text(String str) {
        this.text = str;
        return this;
    }


    public Tooltip text(int rsrc) {
        this.text = context.getResources().getString(rsrc);
        return this;
    }


    public Tooltip textSizeSp(int size) {
        this.textSizeSp = size;
        return this;
    }


    public Tooltip padding(int size) {
        this.padding = size;
        return this;
    }


    public Tooltip backgroundColor(int colorRsrc) {
        backgroundColor = context.getResources().getColor(colorRsrc);
        return this;
    }


    public Tooltip backgroundColorRGB(int color) {
        backgroundColor = color;
        return this;
    }


    public Tooltip after(Tooltip other) {
        other.after = this;
        return this;
    }


    /**
     * Aligns the tooltip with the anchor.
     *
     * @param gravity possible values: {@link Gravity#START}, {@link Gravity#CENTER}, {@link Gravity#END}
     *
     * @return
     */
    private Tooltip align(int gravity) {
        this.gravity = gravity;
        return this;
    }


    public Tooltip left() {
        return align(Gravity.START);
    }


    public Tooltip center() {
        return align(Gravity.CENTER);
    }


    public Tooltip right() {
        return align(Gravity.END);
    }


    /**
     * Sets a preference key that is set to true when the {@link Tooltip} was shown and that has to be unset in order to show this {@link Tooltip}.
     *
     * @param key the preferences key to keep track of whether the {@link Tooltip} has been shown
     *
     * @return the Tooltip
     */
    public Tooltip iff(String key) {
        this.ifPrefsKey = PREFS_PREFIX + key;
        return this;
    }


    public void show() {

        if ((!showAllTooltips) && (ifPrefsKey != null)) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            if (prefs.contains(ifPrefsKey)) {
                return;
            }

            prefs.edit().putBoolean(ifPrefsKey, true).apply();
        }

        anchor.post(() -> {
            DisplayMetrics m = context.getResources().getDisplayMetrics();
            float tsf = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSizeSp, m);
            int textViewHeight = (int) Math.round(tsf);

            final Rect anchorCoords = getScreenCoords(anchor);

            LinearLayout content = getViewProgrammatically(text, padding, textSizeSp, textViewHeight);
            PopupWindow pwc = new PopupWindow(content, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            pwc.setBackgroundDrawable(new ArrowDrawable(content, anchorCoords));
            pwc.setOutsideTouchable(true);

            content.getRootView().setOnClickListener(e -> {
                pwc.dismiss();
                if (after != null) {
                    after.show();
                }
            });

            final int halfWidth = anchorCoords.width() / 2;
            final int anchorMiddle = anchorCoords.left + halfWidth;
            final int screenMiddle = m.widthPixels / 2;
            final int anchorLeftOfMiddle = screenMiddle - anchorMiddle;
            final int popupHeightPx = Utils.getMeasuredHeight(m, content);

            final int flag;
            final int xxx;
            switch (gravity) {
                case Gravity.START: {
                    flag = Gravity.START;
                    xxx = anchorCoords.left;
                    break;
                }
                case Gravity.END: {
                    flag = Gravity.END;
                    xxx = m.widthPixels - anchorCoords.right;
                    break;
                }
                case Gravity.CENTER:
                default: {
                    flag = Gravity.CENTER;
                    xxx = -anchorLeftOfMiddle;
                    break;
                }
            }

            final int offset;
            if (below) {
                offset = anchorCoords.height();
            } else {
                offset = -popupHeightPx - arrowHeight;
            }

            final int yyy = anchorCoords.top + offset;

            pwc.showAtLocation(anchor, Gravity.TOP | flag, xxx, yyy);

            animate(content);
        });
    }


    public void animate(LinearLayout content) {
        final int delayMillis = 75;
        final float amount = 10f;
        int startMs = 250;
        anchor.postDelayed(() -> ObjectAnimator.ofFloat(content, "translationX", amount).setDuration(delayMillis).start(), startMs);
        startMs += delayMillis;
        anchor.postDelayed(() -> ObjectAnimator.ofFloat(content, "translationX", -amount).setDuration(delayMillis * 2).start(), startMs);
        startMs += delayMillis * 2;
        anchor.postDelayed(() -> ObjectAnimator.ofFloat(content, "translationX", 0).setDuration(delayMillis).start(), startMs);
    }


    private Rect getScreenCoords(View v) {
        Rect r = new Rect();
        v.getDrawingRect(r);

        int[] loc = new int[2];
        v.getLocationOnScreen(loc);

        r.left = loc[0];
        r.top = loc[1];
        r.right += r.left;
        r.bottom += r.top;

        return r;
    }


    private LinearLayout getViewProgrammatically(String str, int padding, int textSizePx, int textViewHeight) {
        LinearLayout ll = new LinearLayout(context);
        ll.setGravity(Gravity.CENTER);
        ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ll.setPadding(arrowHeight, arrowHeight, arrowHeight, arrowHeight);

        LinearLayout l1 = new LinearLayout(context);
        l1.setGravity(Gravity.CENTER);
        l1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        l1.setBackgroundColor(backgroundColor);
        l1.setPadding(padding, padding, padding, padding);

        ll.addView(l1);

        TextView tv = new TextView(context);
        tv.setGravity(Gravity.CENTER);
        tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setBackgroundColor(backgroundColor);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizePx);

        tv.setText(str);

        l1.addView(tv);
        return ll;
    }


    /**
     * Draws an "arrow" - i.e. a rotated square of which only one half shows.
     */
    private class ArrowDrawable extends Drawable {
        private final View view;
        private final Rect anchorCoords;
        private final Paint paint;


        public ArrowDrawable(View view, Rect anchorCoords) {
            this.view = view;
            this.anchorCoords = anchorCoords;
            paint = new Paint();
        }


        @Override
        public void draw(@NonNull Canvas canvas) {
            paint.setColor(backgroundColor);

            int smallerWidth = Math.min(view.getWidth(), anchorCoords.width());

            final int halfWidth = smallerWidth / 2;
            final int xTranslation;
            switch (gravity) {
                case Gravity.START: {
                    xTranslation = halfWidth;
                    break;
                }
                case Gravity.END: {
                    xTranslation = view.getWidth() - halfWidth;
                    break;
                }
                default:
                case Gravity.CENTER: {
                    xTranslation = view.getWidth() / 2;
                    break;
                }
            }

            int yTranslation = 0;
            if (!below) {
                yTranslation = view.getHeight() - (2 * arrowHeight);
            }

            int sqW = Math.round((float) arrowHeight * 1.4f);

            canvas.translate(xTranslation, yTranslation);
            canvas.rotate(45);
            canvas.drawRect(0, 0, sqW, sqW, paint);
        }


        private float getWidth() {
            return 100;
        }


        private float getHeight() {
            return 100;
        }


        @Override
        public void setAlpha(int alpha) {

        }


        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {

        }


        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }
}
