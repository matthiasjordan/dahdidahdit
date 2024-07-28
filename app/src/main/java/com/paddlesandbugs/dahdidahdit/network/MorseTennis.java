/****************************************************************************
    Dahdidahdit - an Android Morse trainer
    Copyright (C) 2021-2024 Matthias Jordan <matthias@paddlesandbugs.com>

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

package com.paddlesandbugs.dahdidahdit.network;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.util.function.Consumer;

import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.base.LearningValue;
import com.paddlesandbugs.dahdidahdit.network.mopp.Packet;
import com.paddlesandbugs.dahdidahdit.tennis.TennisMachine;

public class MorseTennis {

    private static final String LOG_TAG = "MORTEN";

    private final TennisMachine machine;

    private final Consumer<String> sender;

    private final Consumer<String> guidePrinter;

    private final Consumer<TennisMachine.GameState> uiUpdater;

    private volatile LearningValue wpm;

    private volatile Context context;

    private int lastDxSpeedWpm;

    private int lastUsSpeedWpm = -1;


    public MorseTennis(Consumer<String> sender, Consumer<String> guidePrinter, Consumer<TennisMachine.GameState> uiUpdater) {
        this.sender = sender;
        this.guidePrinter = guidePrinter;
        this.uiUpdater = uiUpdater;
        this.machine = new TennisMachine(new Client(), (short) 0, (short) 0);
    }


    public void handleReceived(Packet packet) {
        lastDxSpeedWpm = packet.getWpm();
        Log.d(LOG_TAG, "Last DX speed is " + lastDxSpeedWpm + " wpm");
        machine.onMessageReceive(packet.getCharacters().asString());
    }


    public void enqueueWord(MorseCode.CharacterList currentWord) {
        machine.onMessageTransmit(currentWord.asString());

        if (lastUsSpeedWpm != -1) {
            // last us speed was set, so write back.
            wpm.set(lastUsSpeedWpm);
            lastUsSpeedWpm = -1;
        }
    }


    public MorseTennis start(Activity context, LearningValue wpm) {
        this.context = context;
        this.wpm = wpm;

        machine.start();
        return this;
    }


    public MorseTennis stop() {
        machine.stop();
        return this;
    }


    /**
     * For testing.
     *
     * @return the WPM settings for us and DX
     */
    int[] getWpm() {
        int[] wpms = new int[2];
        wpms[0] = lastUsSpeedWpm;
        wpms[1] = lastDxSpeedWpm;
        return wpms;
    }


    private class Client implements TennisMachine.Client {

        @Override
        public void log(String msg) {
            Log.i(LOG_TAG, msg);
        }


        @Override
        public void send(String s) {
            sender.accept(s);
        }


        @Override
        public void printInlineText(String msgId) {
            String msg;
            switch (msgId) {
                case "intro": {
                    msg = i18n(R.string.action_mopp_morsetennis_intro);
                    break;
                }
                case "gameStarts": {
                    msg = i18n(R.string.action_mopp_morsetennis_game_starts);
                    break;
                }
                case "waitForDx": {
                    msg = i18n(R.string.action_mopp_morsetennis_wait_for_dx);
                    break;
                }
                case "giveWordTwice": {
                    msg = i18n(R.string.action_mopp_morsetennis_give_word_twice);
                    break;
                }
                case "challengePassed": {
                    msg = i18n(R.string.action_mopp_morsetennis_challenge_passed);
                    break;
                }
                case "challengeFailed": {
                    msg = i18n(R.string.action_mopp_morsetennis_challenge_failed);
                    break;
                }
                case "usendedgame": {
                    msg = i18n(R.string.action_mopp_morsetennis_usendedgame);
                    break;
                }
                case "dxendedgame": {
                    msg = i18n(R.string.action_mopp_morsetennis_dxendedgame);
                    break;
                }
                case "outro": {
                    msg = i18n(R.string.action_mopp_morsetennis_outro);
                    break;
                }
                default: {
                    msg = "";
                }
            }
            guidePrinter.accept(msg);
        }


        private String i18n(int id) {
            return MorseTennis.this.context.getResources().getString(id);
        }


        @Override
        public void printScore(TennisMachine.GameState g) {
            uiUpdater.accept(g);
        }


        @Override
        public void challengeSound(boolean ok) {

        }


        @Override
        public void onChallengeReceived() {
            lastUsSpeedWpm = wpm.get();
            wpm.set(lastDxSpeedWpm);
            Log.i(LOG_TAG, "Changed WPM from " + lastUsSpeedWpm + " to DX's speed of " + lastDxSpeedWpm + " wpm");
        }
    }
}
