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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.Utils;

public abstract class IntroScreen extends AppCompatActivity {

    public interface Supplier {
        View get();

        void becameVisible();
    }

    private LinearLayout pageDots;
    private final ArrayList<Supplier> screenSuppliers = new ArrayList<>();
    private Button backButton;
    private Button nextButton;
    private ViewPager vp;
    private MyViewPagerAdapter myvpAdapter;
    private final AtomicInteger launchMainCalls = new AtomicInteger();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<Supplier> screens = getViews();
        this.screenSuppliers.addAll(screens);

        setContentView(R.layout.intro_screen);
        vp = (ViewPager) findViewById(R.id.view_pager);
        pageDots = (LinearLayout) findViewById(R.id.layoutBars);
        backButton = (Button) findViewById(R.id.back);
        nextButton = (Button) findViewById(R.id.next);
        if (screens.size() <= 1) {
            nextButton.setText(R.string.intro_screen_start);
        }

        myvpAdapter = new MyViewPagerAdapter();
        vp.setAdapter(myvpAdapter);
        vp.addOnPageChangeListener(viewPagerPageChangeListener);
        drawPageDots(0);
    }


    protected abstract List<Supplier> getViews();


    public void next(View v) {
        int i = getItem(+1);
        if (i < screenSuppliers.size()) {
            vp.setCurrentItem(i);
        } else {
            launchMain();
        }
    }


    private void drawPageDots(int screenNo) {

        pageDots.removeAllViews();

        for (int i = 0; (i < screenSuppliers.size()); i++) {
            ImageView iv = new ImageView(this);
            if (i == screenNo) {
                // set marker
                Drawable mWrappedDrawable = getDrawable(R.drawable.ic_intro_screen_bullet, R.color.theme_secondary_darker);
                iv.setImageDrawable(mWrappedDrawable);
            } else {
                Drawable mWrappedDrawable = getDrawable(R.drawable.ic_intro_screen_bullet, R.color.white);
                iv.setImageDrawable(mWrappedDrawable);
            }
            iv.setPadding(6, 0, 6, 0);
            pageDots.addView(iv);
        }

    }


    private Drawable getDrawable(int bulletRsrc, int color) {
        Drawable mDrawable = ContextCompat.getDrawable(this, bulletRsrc);
        Drawable mWrappedDrawable = mDrawable.mutate();
        mWrappedDrawable = DrawableCompat.wrap(mWrappedDrawable);
        DrawableCompat.setTint(mWrappedDrawable, getResources().getColor(color));
        DrawableCompat.setTintMode(mWrappedDrawable, PorterDuff.Mode.SRC_IN);
        return mWrappedDrawable;
    }


    private int getItem(int i) {
        return vp.getCurrentItem() + i;
    }


    protected abstract void launchMain();


    private void internalLaunchMain() {
        if (launchMainCalls.incrementAndGet() == 1) {
            launchMain();
        }
    }


    private final ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        private boolean nextSwipeIsLaunchMain;


        @Override
        public void onPageSelected(int position) {
            drawPageDots(position);
            if (position == screenSuppliers.size() - 1) {
                nextButton.setText(R.string.intro_screen_start);
            } else {
                nextButton.setText(getString(R.string.intro_screen_next));
            }
        }


        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            screenSuppliers.get(position).becameVisible();
            if ((position == screenSuppliers.size() - 1) && (positionOffset == 0)) {
                if (nextSwipeIsLaunchMain) {
                    internalLaunchMain();
                }
                nextSwipeIsLaunchMain = true;
            } else {
                nextSwipeIsLaunchMain = false;
            }
        }


        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };


    public void onBackButton(View view) {
        int i = vp.getCurrentItem();
        if (i > 0) {
            vp.setCurrentItem(i - 1);
        }
    }


    public void onNextButton(View view) {
        int i = vp.getCurrentItem();
        final int maxScreen = screenSuppliers.size() - 1;
        if (i < maxScreen) {
            vp.setCurrentItem(i + 1);
        }
        if (i == maxScreen) {
            internalLaunchMain();
        }
    }


    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater inflater;


        public MyViewPagerAdapter() {
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = screenSuppliers.get(position).get();
            container.addView(view);
            return view;
        }


        @Override
        public int getCount() {
            return screenSuppliers.size();
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View v = (View) object;
            container.removeView(v);
        }


        @Override
        public boolean isViewFromObject(View v, Object object) {
            return v == object;
        }
    }


    /**
     * Builds an intro screen page.
     */
    public static class Builder {

        public interface VisibilityListener {
            void visibilityChanged(int visibility);
        }

        /**
         * Default font size in SP.
         */
        public static final int DEFAULT_FONT_SIZE_SP = 20;


        private final Context context;

        private int imageRsrc = -1;

        private RelativeLayout.LayoutParams imLayout = new RelativeLayout.LayoutParams(150, 150);

        private CharSequence text = null;

        private int fontSizeSP = DEFAULT_FONT_SIZE_SP;

        private int bgColAttr = R.attr.colorPrimary;

        private final int textColAttr = R.attr.colorOnPrimary;

        private int headline = -1;

        private View subView = null;

        private VisibilityListener visibilityListener;


        @SuppressLint("ResourceType")
        public Builder(Context context) {
            this.context = context;
        }


        public Builder image(int rsrc) {
            this.imageRsrc = rsrc;
            return this;
        }


        public Builder size(int width, int height) {
            this.imLayout = new RelativeLayout.LayoutParams(width, height);
            return this;
        }


        public Builder headline(int rsrc) {
            this.headline = rsrc;
            return this;
        }


        public Builder text(int rsrc) {
            this.text = context.getResources().getText(rsrc);
            return this;
        }


        public Builder text(String text) {
            this.text = text;
            return this;
        }


        public Builder size(int fontSizeSP) {
            this.fontSizeSP = fontSizeSP;
            return this;
        }


        public Builder bgCol(int colAttr) {
            this.bgColAttr = colAttr;
            return this;
        }


        public Builder view(View v) {
            this.subView = v;
            return this;
        }


        public Builder add(VisibilityListener listener) {
            this.visibilityListener = listener;
            return this;
        }


        public Supplier supply() {
            return new StaticSupplier(build());
        }


        /**
         * Styles the text.
         *
         * @param t          the text
         * @param fontSizeSP font size in SP
         * @param textColId  the color ID (if theme color, use {@link Utils#getThemeColor(Context, int)} to resolve color ID from attr
         */
        public static void style(TextView t, int fontSizeSP, int textColId) {
            t.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeSP);
            t.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            t.setLineSpacing(0.0f, 1.2f);
            int colData = t.getContext().getResources().getColor(textColId);
            t.setTextColor(colData);
        }


        /**
         * Styles the text.
         *
         * @param s         the spinner
         * @param textColId the color ID (if theme color, use {@link Utils#getThemeColor(Context, int)} to resolve color ID from attr
         */
        public static void style(Spinner s, int textColId) {
            s.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            int colData = s.getContext().getResources().getColor(textColId);
            s.setPopupBackgroundResource(textColId);
        }


        @SuppressLint("ResourceType")
        public View build() {
            int lowestId = -1;
            int id = 99900;
            RelativeLayout rl = new RelativeLayout(context);
            RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rl.setLayoutParams(rlParams);
            //setBackgroundColor(rl);
            rl.setPadding(50, 10, 50, 10);

            TextView headlinev = null;

            final int textColId = Utils.getThemeColor(context, this.textColAttr);

            if (headline != -1) {
                headlinev = new TextView(context);
                headlinev.setId(++id);
                headlinev.setText(headline);
                style(headlinev, fontSizeSP + 10, textColId);
                final RelativeLayout.LayoutParams tvLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                tvLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
                tvLayout.setMargins(0, 10, 0, 10);
                headlinev.setLayoutParams(tvLayout);
                rl.addView(headlinev);
                lowestId = id;
            }

            ImageView im = null;
            if (imageRsrc != -1) {
                im = new ImageView(context);
                im.setId(++id);
                im.setImageResource(imageRsrc);
                imLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
                if (lowestId != -1) {
                    imLayout.addRule(RelativeLayout.BELOW, lowestId);
                }
                im.setLayoutParams(imLayout);
                rl.addView(im);
                lowestId = id;
            }

            TextView tv = null;
            if (text != null) {
                tv = new TextView(context);
                tv.setId(++id);
                tv.setText(text);
                style(tv, fontSizeSP, textColId);
                final RelativeLayout.LayoutParams tvLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                if (lowestId != -1) {
                    tvLayout.addRule(RelativeLayout.BELOW, lowestId);
                }
                tvLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
                tvLayout.setMargins(0, 10, 0, 10);
                tv.setLayoutParams(tvLayout);
                rl.addView(tv);
                lowestId = id;
            }

            if (subView != null) {
                subView.setId(++id);
                final RelativeLayout.LayoutParams svLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                if (lowestId != -1) {
                    svLayout.addRule(RelativeLayout.BELOW, lowestId);
                }
                svLayout.addRule(RelativeLayout.ALIGN_LEFT);
                subView.setLayoutParams(svLayout);
                rl.addView(subView);
                lowestId = id;
            }

            ScrollView sv = new ScrollView(context) {
                @Override
                public void setVisibility(int visibility) {
                    super.setVisibility(visibility);
                    if (visibilityListener != null) {
                        visibilityListener.visibilityChanged(visibility);
                    }
                }
            };
            sv.addView(rl);
            return sv;
        }


        private void setBackgroundColor(RelativeLayout rl) {
            int colorId = Utils.getThemeColor(context, bgColAttr);
            rl.setBackgroundResource(colorId);
        }


    }

    public static class StaticSupplier implements Supplier {
        private final View view;


        public StaticSupplier(View view) {
            this.view = view;
        }


        @Override
        public View get() {
            return view;
        }


        @Override
        public void becameVisible() {
            // Nothing
        }
    }
}