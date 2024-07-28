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

package com.paddlesandbugs.dahdidahdit.base;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.params.GeneralParameters;
import com.paddlesandbugs.dahdidahdit.sound.MorsePlayer;
import com.paddlesandbugs.dahdidahdit.sound.MorsePlayerI;

public abstract class AbstractTrainerActivity extends AbstractNavigationActivity {


    private static final String LOG_TAG = AbstractTrainerActivity.class.getSimpleName();


    private MorsePlayerI player;

    private TextView headerText;
    private ImageView stopButton;
    private ImageView startButton;
    private ImageView pauseButton;

    private AudioManager am;

    private LearningStrategy.SessionConfig sessionConfig = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        stopButton = findViewById(R.id.imageStop);
        startButton = findViewById(R.id.imageStart);
        pauseButton = findViewById(R.id.imagePause);

        headerText = findViewById(R.id.textCopyTrainHeader);

        new Tooltip(this).below(startButton).text(R.string.tooltip_trainer_start).iff("startButton").show();

        onCreateCallback();

        setModeStop();

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        TrainerViewModel model = new ViewModelProvider(this).get(TrainerViewModel.class);
        final LiveData<MorsePlayerI> playerLD = model.getPlayer();
        if (playerLD != null) {
            playerLD.observe(this, p -> {
                this.player = p;
                if (p != null) {
                    switch (p.getMode()) {
                        case PAUSED: {
                            setModePause();
                            break;
                        }
                        case STOPPED: {
                            setModeStop();
                            break;
                        }
                        case PLAYING: {
                            setModePlay();
                            break;
                        }
                        default:
                    }
                }
            });
        }

    }


    @Override
    protected int getLayoutID() {
        return R.layout.activity_copy_trainer;
    }


    protected abstract void onCreateCallback();

    protected abstract GeneralParameters createParams();


    @Override
    protected void onResume() {
        super.onResume();

        setupPlay();

        sessionConfig = updateSessionConfig();

        if (sessionConfig == null) {
            setSessionEnd();
        } else {
            TextView durationText = findViewById(R.id.textCopyTrainDuration);
            final int sessionS = sessionConfig.morsePlayerConfig.sessionS;
            String durationStr = getResources().getQuantityString(R.plurals.text_duration, sessionS, sessionS);
            durationText.setText(durationStr);
        }
    }


    private void setSessionEnd() {
        startButton.setEnabled(false);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);

        onSetSessionEnd();
    }


    /**
     * Called when {@link LearningStrategy#getSessionConfig()} returns null, indicating that nothing is left to be played.
     */
    protected void onSetSessionEnd() {
        // Nothing
    }


    private LearningStrategy.SessionConfig updateSessionConfig() {
        LearningStrategy strat = getLearningStrategy();
        return strat.getSessionConfig();
    }


    protected abstract LearningStrategy getLearningStrategy();

    /**
     * Called to start the grading activity.
     *
     * @param text the text that has been morsed
     * @param misc the misc object containing additional information. Might be null.
     * @param a    reference to the activity
     */
    protected abstract void callGrading(String text, Object misc, Activity a);


    @Override
    protected void onStop() {
        super.onStop();
        onStopPlay(null);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    private void setupPlay() {
        headerText.setText(R.string.copy_trainer_header);
    }


    public void onStartPlay(View view) {
        if ((player == null) || (player.getMode() == MorsePlayer.Mode.STOPPED)) {

            final AudioManager.OnAudioFocusChangeListener afcl = new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange != AudioManager.AUDIOFOCUS_GAIN) {
                        onPausePlay(pauseButton);
                    }
                }
            };

            int res = am.requestAudioFocus(afcl, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE);

            if (res == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
                Toast.makeText(this, R.string.cannot_get_audiofocus, Toast.LENGTH_SHORT).show();
                return;
            }

            sessionConfig = updateSessionConfig();
            if (sessionConfig == null) {
                setSessionEnd();
                return;
            }

            player = getPlayer(sessionConfig);
            player.setFireFinishedOnStop(true);
            player.setFinishedCallback(new MorsePlayer.FinishedCallback() {
                @Override
                public void finished(String text) {
                    if (!text.isEmpty()) {
                        Activity a = AbstractTrainerActivity.this;
                        callGrading(text, sessionConfig.misc, a);
                        a.finish();
                    }
                }
            });
            player.setStopCallback(new Runnable() {
                @Override
                public void run() {
                    Log.d(LOG_TAG, "Player stopped (callback)");
                    am.abandonAudioFocus(afcl);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setModeStop();
                            setupPlay();
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        }
                    });
                }
            });

            TrainerViewModel model = new ViewModelProvider(this).get(TrainerViewModel.class);
            model.setPlayer(player);
        }

        setModePlay();

        int vol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (vol == 0) {
            Toast.makeText(this, R.string.play_while_muted, Toast.LENGTH_LONG).show();
        }

        player.play();
    }


    @NonNull
    protected MorsePlayerI getPlayer(LearningStrategy.SessionConfig sessionConfig) {
        return new MorsePlayer(sessionConfig.morsePlayerConfig);
    }


    private void setModePlay() {
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        stopButton.setEnabled(true);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        headerText.setText(R.string.copy_trainer_header_sending);
    }


    public void onStopPlay(View view) {
        stopButton.setEnabled(false);
        pauseButton.setEnabled(false);
        if (player != null) {
            player.stop();
        }
    }


    private void setModeStop() {
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
    }


    public void onPausePlay(View view) {
        setModePause();
        if (player != null) {
            player.pause();
        }
    }


    private void setModePause() {
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(true);
        if ((player != null) && (player.getMode() == MorsePlayer.Mode.PAUSED)) {
            headerText.setText(R.string.copy_trainer_header);
        } else {
            headerText.setText(R.string.copy_trainer_header_sending);
        }
    }


    protected AbstractLearningStrategy.ParameterProvider createParameterProvider() {
        return new TrainerParameterProvider();
    }


    public class TrainerParameterProvider implements AbstractLearningStrategy.ParameterProvider {

        @Override
        public GeneralParameters get() {
            return createParams();
        }

    }


    public static class TrainerViewModel extends ViewModel {

        MutableLiveData<MorsePlayerI> player;


        public LiveData<MorsePlayerI> getPlayer() {
            return player;
        }


        public void setPlayer(MorsePlayerI player) {
            this.player = new MutableLiveData<>();
            this.player.setValue(player);
        }

        // https://developer.android.com/topic/libraries/architecture/viewmodel
    }


}