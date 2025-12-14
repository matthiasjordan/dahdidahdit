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

package com.paddlesandbugs.dahdidahdit.sound;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.paddlesandbugs.dahdidahdit.params.GeneralFadedParameters;
import com.paddlesandbugs.dahdidahdit.params.GeneralParameters;
import com.paddlesandbugs.dahdidahdit.text.TextGenerator;

public interface MorsePlayerI {

    interface FinishedCallback {
        void finished(String text);
    }

    enum Mode {
        STOPPED, //
        PAUSED, //
        PLAYING //
    }

    class Config {
        public TextGenerator textGenerator;

        public int sessionS;
        private int startPauseMs;
        private int endPauseMs;

        public MorseTiming timing;
        public int qsb;
        public int qrm;
        public int qrn;

        public int freqDit;
        public int freqDah;

        public int syllablePauseMs;

        public boolean chirp = false;
        public int qlf = 1;


        public Config() {
            //
        }


        private Config(GeneralFadedParameters pf) {
            from(pf);
        }


        public Config from(GeneralFadedParameters pf) {
            timing = MorseTiming.get(pf.getWpm(), pf.getEffWPM());
            qsb = pf.getQSB();
            qrm = pf.getQRM();
            qrn = pf.getQRN();
            return this;
        }


        public Config from(Context context, GeneralParameters p) {
            setStartPauseMs(context, p.getStartPauseS() * 1000);
            setEndPauseMs(context, 0);
            sessionS = p.getSessionS();
            from(p.current());
            return this;
        }


        public Config from(com.paddlesandbugs.dahdidahdit.Config gc) {
            freqDah = gc.freqDah;
            freqDit = gc.freqDit;
            return this;
        }

        public void setStartPauseMs(Context context, int explicitPauseMs) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            startPauseMs = Math.max(explicitPauseMs, prefs.getInt("morseplayer_lead_time_ms", 0));
        }

        public int getStartPauseMs() {
            return startPauseMs;
        }

        public void setEndPauseMs(Context context, int explicitPauseMs) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            endPauseMs = Math.max(explicitPauseMs, prefs.getInt("morseplayer_end_time_ms", 0));
        }


        public int getEndPauseMs() {
            return endPauseMs;
        }


    }

    void setBuffer(int framesPerBufferInt);

    void setSampleRate(int sampleRate);

    void setFinishedCallback(FinishedCallback finishedCallback);

    void setStopCallback(Runnable finishedCallback);

    Mode getMode();

    void setFireFinishedOnStop(boolean value);

    void play();

    void pause();

    void stop();

    void close();


}