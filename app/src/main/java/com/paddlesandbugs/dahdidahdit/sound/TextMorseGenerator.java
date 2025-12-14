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

import com.paddlesandbugs.dahdidahdit.Const;
import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.text.TextGenerator;

import java.util.ArrayList;
import java.util.Collections;

public class TextMorseGenerator implements MorseGenerator {

    public static class Config {
        public TextGenerator textGen;
        public int startPauseMs;
        public int syllablePauseMs;
        public int freqDit;
        public int freqDah;
        public boolean chirp = false;
        public int qlf = 1;
        public MorseTiming timing;
        public int endPauseMs;
    }


    private static final String LOG_TAG = "MorseGenerator";


    private int startPauseMs;
    private final short[] startPausePart;
    private final int startPausePartMs;


    private final TextGenerator textGen;

    private boolean withinWord = false;


    private final SignGenerator sg;


    public TextMorseGenerator(Config config) {
        this.textGen = config.textGen;
        this.startPauseMs = config.startPauseMs;

        final SoundGenerator sg;
        if (config.chirp) {
            sg = new ChirpSoundGenerator(Const.SAMPLES_PER_S);
        } else {
            sg = new IntSoundGenerator(Const.SAMPLES_PER_S);
        }

        if (config.qlf > 1) {
            final float maxEps = 0.5f;
            final float eps = (maxEps / 4.0f) * (config.qlf - 1);
            this.sg = new QLFSignGenerator(sg, eps, config.freqDit, config.freqDah, config.timing, config.syllablePauseMs);
        } else {
            this.sg = new PerfectSignGenerator(sg, config.freqDit, config.freqDah, config.timing, config.syllablePauseMs);
        }

        this.startPausePartMs = 250;
        startPausePart = sg.genTone(config.freqDit, 0.0f, startPausePartMs).getFull();
    }


    public Part generate() {
        Part part;
        if (startPauseMs > 0) {
            startPauseMs -= startPausePartMs;
            if (startPauseMs > startPausePartMs) {
                part = new Part();
                part.sample = startPausePart;
                part.isPrinted = false;
                part.text = null;
                return part;
            }
        }

        if (textGen.hasNext()) {
            part = new Part();

            TextGenerator.TextPart textPart = textGen.next();
            part.isPrinted = textPart.isPrinted();
            MorseCode.CharacterData d = textPart.getChar();
            part.text = new MorseCode.MutableCharacterList(Collections.singletonList(d));

            if (d.equals(MorseCode.SYLLABLEBREAK)) {
                short[] syllableBreak = sg.genSyllableBreak();
                part.sample = syllableBreak;
            } else if (d.equals(MorseCode.WORDBREAK)) {
                short[] wordBreak = sg.genWordBreak();
                part.sample = wordBreak;
                withinWord = false;
            } else {
                if (withinWord) {
                    short[] charBreak = sg.genCharBreak();
                    short[] sample = process(d.getCw());
                    part.sample = new short[charBreak.length + sample.length];
                    copyTo(part.sample, 0, charBreak);
                    copyTo(part.sample, charBreak.length, sample);
                } else {
                    short[] sample = process(d.getCw());
                    part.sample = sample;
                }
                withinWord = true;
            }

        } else {
            part = null;
        }
        return part;
    }


    /**
     * Closes the underlying {@link TextGenerator}, making the {@link TextMorseGenerator} cease operation at the next sensible point in time.
     */
    public void close() {
        textGen.close();
    }


    private short[] process(String morse) {
        ArrayList<short[]> samples = new ArrayList<>();
        int totalLen = 0;
        boolean lastSign = false;

        for (int i = 0; (i < morse.length()); i++) {

            if (lastSign) {
                short[] sample = sg.genSignBreak();
                samples.add(sample);
                totalLen += sample.length;
            }

            char c = morse.charAt(i);
            switch (c) {
                case '.': {
                    short[] sample = sg.genDit();
                    samples.add(sample);
                    totalLen += sample.length;
                    lastSign = true;
                    break;
                }
                case '-': {
                    short[] sample = sg.genDah();
                    samples.add(sample);
                    totalLen += sample.length;
                    lastSign = true;
                    break;
                }
                case ' ': {
                    short[] sample = sg.genCharBreak();
                    samples.add(sample);
                    totalLen += sample.length;
                    break;
                }
                case '\n': {
                    short[] sample = sg.genWordBreak();
                    samples.add(sample);
                    totalLen += sample.length;
                    break;
                }
                default: {
                }
            }
        }

        short[] res = new short[totalLen];
        int to = 0;
        for (short[] sample : samples) {
            to = copyTo(res, to, sample);
        }

        return res;
    }


    private int copyTo(short[] dest, int to, short[] orig) {
        for (int i = 0; (i < orig.length); i++) {
            dest[to++] = orig[i];
        }
        return to;
    }


}
