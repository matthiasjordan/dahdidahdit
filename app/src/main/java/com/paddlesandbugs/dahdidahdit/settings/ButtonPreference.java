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
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.paddlesandbugs.dahdidahdit.R;

public class ButtonPreference extends Preference {


    public ButtonPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setWidgetLayoutResource(R.layout.button_preference);
    }


    public ButtonPreference(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWidgetLayoutResource(R.layout.button_preference);
    }




    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        final String buttonKey = getKey();
        final CharSequence title = getTitle();
        final int keyCode = getSharedPreferences().getInt(buttonKey, 0);

        TextView textView = (TextView) holder.findViewById(R.id.keycodetextview);
        textView.setText(Integer.toString(keyCode));

        Button button = (Button) holder.findViewById(R.id.button);
        button.setText(R.string.record_paddle_key_code);
        button.setClickable(true);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context activity = getContext();
                if (activity instanceof SettingsActivity) {
                    ((SettingsActivity) activity).setKeyEventFunction(keyEvent -> {
                        final int keyCode = keyEvent.getKeyCode();
                        getSharedPreferences().edit().putInt(buttonKey, keyCode).apply();
                        textView.setText(Integer.toString(keyCode));
                        return true;
                    });

                    Toast.makeText(activity, R.string.record_paddle_prompt, Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
