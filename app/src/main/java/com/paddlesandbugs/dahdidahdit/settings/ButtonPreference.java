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

package com.paddlesandbugs.dahdidahdit.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.paddlesandbugs.dahdidahdit.R;

import java.util.ArrayList;

public class ButtonPreference extends Preference {


    public ButtonPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setWidgetLayoutResource(R.layout.button_preference);
    }


    public ButtonPreference(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWidgetLayoutResource(R.layout.button_preference);
    }

    protected String getKeyCodeDisplayLabel(int keyCode, boolean isMouse) {
        if (isMouse) {
            ArrayList<String> buttons = new ArrayList<>();

            if ((keyCode & MotionEvent.BUTTON_BACK) > 0) buttons.add("BUTTON_BACK");
            if ((keyCode & MotionEvent.BUTTON_FORWARD) > 0) buttons.add("BUTTON_FORWARD");
            if ((keyCode & MotionEvent.BUTTON_PRIMARY) > 0) buttons.add("BUTTON_PRIMARY");
            if ((keyCode & MotionEvent.BUTTON_SECONDARY) > 0) buttons.add("BUTTON_SECONDARY");
            if ((keyCode & MotionEvent.BUTTON_TERTIARY) > 0) buttons.add("BUTTON_TERTIARY");
            if ((keyCode & MotionEvent.BUTTON_STYLUS_PRIMARY) > 0) buttons.add("BUTTON_STYLUS_PRIMARY");
            if ((keyCode & MotionEvent.BUTTON_STYLUS_SECONDARY) > 0) buttons.add("BUTTON_STYLUS_SECONDARY");

            return String.join(", ", buttons);
        }

        return KeyEvent.keyCodeToString(keyCode);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        final String buttonKey = getKey();
        final String isMouseKey = buttonKey + "_is_mouse";
        final CharSequence title = getTitle();
        final int keyCode = getSharedPreferences().getInt(buttonKey, 0);
        final boolean isMouse = getSharedPreferences().getBoolean(isMouseKey, false);

        TextView textView = (TextView) holder.findViewById(R.id.keycodetextview);
        textView.setText(getKeyCodeDisplayLabel(keyCode, isMouse));

        Button button = (Button) holder.findViewById(R.id.button);
        button.setText(R.string.record_paddle_key_code);
        button.setClickable(true);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context activity = getContext();
                button.setText((R.string.recording_paddle_key_code));
                if (activity instanceof SettingsActivity) {
                    ((SettingsActivity) activity).setKeyEventFunction(keyEvent -> {
                        final int keyCode = keyEvent.getKeyCode();

                        SharedPreferences.Editor editor = getSharedPreferences().edit();
                        editor.putInt(buttonKey, keyCode);
                        editor.putBoolean(isMouseKey, false);
                        editor.apply();

                        textView.setText(getKeyCodeDisplayLabel(keyCode, false));

                        stopRecording();
                        return true;
                    });

                    ((SettingsActivity) activity).setMotionEventFunction(motionEvent -> {
                        // only capture from external devices, no screen taps
                        if (motionEvent.getSource() == InputDevice.SOURCE_TOUCHSCREEN) return false;

                        final int buttonState = motionEvent.getButtonState();

                        SharedPreferences.Editor editor = getSharedPreferences().edit();
                        editor.putInt(buttonKey, buttonState);
                        editor.putBoolean(isMouseKey, true);
                        editor.apply();

                        textView.setText(getKeyCodeDisplayLabel(buttonState, true));

                        stopRecording();
                        return true;
                    });

                    Toast.makeText(activity, R.string.record_paddle_prompt, Toast.LENGTH_LONG).show();
                }
            }

            public void stopRecording() {
                button.setText(R.string.record_paddle_key_code);

                Context activity = getContext();
                ((SettingsActivity) activity).setMotionEventFunction(null);
                ((SettingsActivity) activity).setKeyEventFunction(null);
            }
        });

    }
}
