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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.paddlesandbugs.dahdidahdit.Config;
import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.base.AbstractNavigationActivity;
import com.paddlesandbugs.dahdidahdit.base.LearningValue;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;
import com.paddlesandbugs.dahdidahdit.brasspound.AudioHelper;
import com.paddlesandbugs.dahdidahdit.brasspound.Decoder;
import com.paddlesandbugs.dahdidahdit.brasspound.MorseInput;
import com.paddlesandbugs.dahdidahdit.brasspound.PaddleMorseInput;
import com.paddlesandbugs.dahdidahdit.brasspound.StraightMorseInput;
import com.paddlesandbugs.dahdidahdit.network.mopp.MOPPClient;
import com.paddlesandbugs.dahdidahdit.network.mopp.Packet;
import com.paddlesandbugs.dahdidahdit.sound.InstantMorsePlayer;
import com.paddlesandbugs.dahdidahdit.sound.MorsePlayerI;
import com.paddlesandbugs.dahdidahdit.sound.MorseTiming;
import com.paddlesandbugs.dahdidahdit.tennis.TennisMachine;
import com.paddlesandbugs.dahdidahdit.text.StaticTextGenerator;

public class MOPPClientActivity extends AbstractNavigationActivity {

    private static final String LOG_TAG = "MOPPClAct";

    private static final String EXTRA_ADDRESS = "mopp_address";

    private final Mode standardMode = new StandardMode();

    private final Mode morseTennisMode = new MorseTennisMode();

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private TextView tt;

    private ScrollView sv;

    private MorseInput morseInput;

    private MOPPClient moppClient;

    private String addressStr;

    private boolean isTextShown = true;

    private int dxFrequency;

    private Mode mode = standardMode;

    private LearningValue wpm;


    public static void callMe(Context context, NetworkConfig config) {
        Intent intent = new Intent(context, MOPPClientActivity.class);
        intent.putExtra(EXTRA_ADDRESS, config.address);
        context.startActivity(intent);
    }


    private void updateShowTextMenuItem(boolean isTextShown, MenuItem i) {
        if (isTextShown) {
            i.setIcon(R.drawable.baseline_visibility_off_24);
            tt.setVisibility(View.VISIBLE);
        } else {
            i.setIcon(R.drawable.baseline_visibility_24);
            tt.setVisibility(View.INVISIBLE);
        }
    }


    private void updatePlayMorseTennisMenuItem(boolean doPlayMorseTennis, MenuItem i) {
        if (doPlayMorseTennis) {
            i.setTitle(R.string.action_mopp_morsetennis_stop);
        } else {
            i.setTitle(R.string.action_mopp_morsetennis_play);
        }
    }


    @Override
    protected int getMenuID() {
        return R.menu.menu_moppclient;
    }


    @Override
    protected String getHelpPageName() {
        return getString(R.string.helpurl_internet);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        setupShowHideMorse(menu);
        setupPlayMorseTennis(menu);

        return true;
    }


    private void setupShowHideMorse(Menu menu) {
        MenuItem i = menu.findItem(R.id.action_show_hide_morse);
        updateShowTextMenuItem(isTextShown, i);

        i.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {


            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                isTextShown = !isTextShown;
                updateShowTextMenuItem(isTextShown, item);
                return true;
            }
        });
    }


    private void setupPlayMorseTennis(Menu menu) {
        MenuItem i = menu.findItem(R.id.action_play_morsetennis);
        updatePlayMorseTennisMenuItem(mode == morseTennisMode, i);

        i.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {


            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                mode.end();
                mode = (mode == morseTennisMode) ? standardMode : morseTennisMode;
                mode.start();
                updatePlayMorseTennisMenuItem(mode == morseTennisMode, item);
                return true;
            }
        });
    }


    @Override
    protected int getLayoutID() {
        return R.layout.activity_brass_pound;
    }


    @Override
    protected int createTitleID() {
        return R.string.mopp_client_title;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            addressStr = extras.getString(EXTRA_ADDRESS);
        }

        takeKeyEvents(true);
        setDefaultKeyMode(DEFAULT_KEYS_DISABLE);
        MainActivity.setActivity(this, MainActivity.MOPPCLIENT);
    }


    @Override
    protected void onResume() {
        super.onResume();

        Log.i(LOG_TAG, "onResume()");

        Config config = new Config();
        config.update(this);

        int freq = config.freqDit;
        AudioHelper.start(this, freq);

        this.dxFrequency = getDxFrequency(config);

        tt = findViewById(R.id.output);
        sv = findViewById(R.id.scroller);

        final Consumer<Exception> exceptionConsumer = e -> {
            runOnUiThread(() -> {
                String msg = getResources().getString(R.string.network_error, e.getMessage());
                Toast.makeText(MOPPClientActivity.this, msg, Toast.LENGTH_LONG).show();
            });
        };

        //
        //
        // Receiving packets
        //
        final Consumer<Packet> packetConsumer = packet -> {
            MorseCode.CharacterList text = packet.getCharacters();
            Log.d(LOG_TAG, "Activity received: " + text.asString() + " with " + packet.getWpm() + " wpm");

            executor.submit(() -> {
                Log.d(LOG_TAG, "Executor starting: " + text.asString() + " with " + packet.getWpm() + " wpm");
                mode.onReceive(packet);
                Log.d(LOG_TAG, "Executor ending: " + text.asString() + " with " + packet.getWpm() + " wpm");
            });
        };

        try {
            Address address = Address.parse(addressStr);
            moppClient = new MOPPClient(this, address, packetConsumer, exceptionConsumer);
        } catch (SocketException | UnknownHostException | ParseException e) {
            Log.e(LOG_TAG, "could not create socket");
            exceptionConsumer.accept(e);
            finish();
        }

        wpm = getWpm();

        if (config.isPaddles) {
            morseInput = new PaddleMorseInput(this, wpm);
        } else {
            morseInput = new StraightMorseInput(this, wpm);
        }

        morseInput.init(new Decoder.CharListener() {

            final MorseCode.MutableCharacterList currentWord = new MorseCode.MutableCharacterList();


            @Override
            public void decoded(MorseCode.CharacterData c) {
                final String plain;
                if (c == null) {
                    plain = "*";
                } else {
                    plain = c.getPlain();
                }
                printSent(plain);

                try {
                    if (c != null) {
                        if (c.equals(MorseCode.WORDBREAK)) {
                            Log.d(LOG_TAG, "Will send " + currentWord);
                            mode.send(currentWord);
                            currentWord.clear();
                        } else {
                            currentWord.add(c);
                        }
                    }
                } catch (IOException e) {
                    Toast.makeText(MOPPClientActivity.this, "Could not send", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }


    @NonNull
    private LearningValue getWpm() {
        final int defaultWpm = getResources().getInteger(R.integer.default_value_wpm_sending);
        return new LearningValue(this, "brasspounder_current_wpm", 1, defaultWpm, 40);
    }


    private void playMorse(Packet text) {
        Log.i(LOG_TAG, "Playing morse code for " + text.getCharacters().asString() + " at " + text.getWpm() + " wpm");
        MorsePlayerI.Config morsePlayerConfig = new MorsePlayerI.Config();
        morsePlayerConfig.timing = MorseTiming.get(text.getWpm(), text.getWpm());
        morsePlayerConfig.sessionS = Integer.MAX_VALUE;
        morsePlayerConfig.textGenerator = new StaticTextGenerator(text.getCharacters(), false);
        morsePlayerConfig.freqDit = dxFrequency;
        morsePlayerConfig.freqDah = dxFrequency;
        final InstantMorsePlayer instantMorsePlayer = new InstantMorsePlayer(morsePlayerConfig);
        instantMorsePlayer.play();
        instantMorsePlayer.await();
    }


    private int getDxFrequency(Config config) {
        final int freqJitter = 20;
        final int freqDelta = new Random().nextInt(freqJitter) - (freqJitter / 2);
        return config.freqDit + freqDelta;
    }


    private void printSent(String message) {
        Spannable s = new SpannableString(message);
        printSpannable(s);
    }


    private void printReceived(Packet packet) {
        final MorseCode.CharacterList text = packet.getCharacters();
        printReceived(text.asString());
    }


    private void printReceived(String message) {
        Spannable s = new SpannableString(message + " ");
        CharacterStyle cs = new ForegroundColorSpan(getColor(R.color.theme_primary_darkest));
        CharacterStyle us = new UnderlineSpan();
        final int end = s.length() - 1;
        s.setSpan(cs, 0, end, 0);
        s.setSpan(us, 0, end, 0);
        printSpannable(s);
    }


    private void printGuideText(String message) {
        Spannable s = new SpannableString(message + " ");

        CharacterStyle csf = new ForegroundColorSpan(getColor(R.color.theme_inverse_TextColor));
        CharacterStyle csb = new BackgroundColorSpan(getColor(R.color.theme_primary_darker));
        //        CharacterStyle us = new UnderlineSpan();
        final int end = s.length() - 1;
        s.setSpan(csf, 0, end, 0);
        s.setSpan(csb, 0, end, 0);
        //        s.setSpan(us, 0, end, 0);
        printSpannable(s);
    }


    private void printSpannable(Spannable s) {
        runOnUiThread(() -> {
            tt.append(s);
            scrollToEnd();
        });
    }


    private void scrollToEnd() {
        sv.post(() -> {
            sv.fullScroll(View.FOCUS_DOWN);
        });
    }


    @Override
    protected void onPause() {
        Log.i(LOG_TAG, "onPause()");
        AudioHelper.stopPlaying();
        AudioHelper.shutdown();
        if (moppClient != null) {
            moppClient.close();
        }
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        morseInput.handleKey(event);
        return super.dispatchKeyEvent(event);
    }


    private interface Mode {

        void start();

        void end();

        void onReceive(Packet packet);

        void send(MorseCode.CharacterList text) throws IOException;
    }


    private class StandardMode implements Mode {

        @Override
        public void start() {

        }


        @Override
        public void end() {

        }


        @Override
        public void onReceive(Packet packet) {
            Log.d(LOG_TAG, "StandardMode received: " + packet.getCharacters().asString() + " with " + packet.getWpm() + " wpm");
            printReceived(packet);
            playMorse(packet);
        }


        @Override
        public void send(MorseCode.CharacterList currentWord) throws IOException {
            moppClient.send(new Packet(currentWord, getWpm().get()), false);
        }
    }

    private class MorseTennisMode implements Mode {
        private final MorseTennis morseTennis;


        public MorseTennisMode() {
            morseTennis = new MorseTennis( //
                    this::sendMessage, //
                    MOPPClientActivity.this::printGuideText, //
                    this::updateStatus);
        }


        private void sendMessage(String msg) {
            try {
                final int wpm = getWpm().get();
                Log.i(LOG_TAG, "Sending via MOPP client: " + msg + " with " + wpm + " wpm");
                String[] words = msg.split(" ");
                int pos = 0;
                for (String word : words) {
                    moppClient.send(new Packet(word, wpm), (pos++ >= 1));
                }
            } catch (IOException e) {
                Toast.makeText(MOPPClientActivity.this, "Network problems", Toast.LENGTH_SHORT).show();
            }
        }


        private void updateStatus(TennisMachine.GameState g) {
            runOnUiThread(() -> {
                updateStationScore(R.id.score_us, g.us);
                updateStationScore(R.id.score_dx, g.dx);
                final View statusView = findViewById(R.id.morse_tennis_status);
                ((TextView) statusView.findViewById(R.id.info)).setText(g.statusTextId);
                Log.i(LOG_TAG, "updateStatus set info to " + g.statusTextId);
            });
        }


        private void updateStationScore(int stationScoreId, TennisMachine.Station station) {
            Log.d(LOG_TAG, "Updating station score on " + stationScoreId + " to " + station);
            final String call = station.call;
            String callText = "";
            String scoreText = "";
            if (call != null) {
                callText = call;
                try {
                    scoreText = Integer.toString(station.points);
                } catch (Exception e) {
                    Log.i(LOG_TAG, e.getMessage());
                }
            }

            Log.d(LOG_TAG, "Updating station score on " + stationScoreId + " to " + callText + "/" + scoreText);
            final View statusView = findViewById(R.id.morse_tennis_status);
            final View stationView = statusView.findViewById(stationScoreId);
            TextView callView = stationView.findViewById(R.id.call);
            TextView scoreView = stationView.findViewById(R.id.score);
            callView.setText(callText);
            scoreView.setText(scoreText);
            Log.d(LOG_TAG, "Updated station score on " + stationScoreId + " -> " + callView.hashCode() + "/" + scoreView.hashCode() + " to " + callText + "/" + scoreText);
        }


        @Override
        public void start() {
            morseTennis.start(MOPPClientActivity.this, MOPPClientActivity.this.wpm);
            findViewById(R.id.morse_tennis_status).setVisibility(View.VISIBLE);
            scrollToEnd();
        }


        @Override
        public void end() {
            findViewById(R.id.morse_tennis_status).setVisibility(View.GONE);
            morseTennis.stop();
        }


        @Override
        public void onReceive(Packet packet) {
            Log.d(LOG_TAG, "MorseTennisMode received: " + packet.getCharacters().asString() + " with " + packet.getWpm() + " wpm");
            printReceived(packet);
            playMorse(packet);
            morseTennis.handleReceived(packet);
        }


        @Override
        public void send(MorseCode.CharacterList currentWord) {
            morseTennis.enqueueWord(currentWord);
        }
    }


}