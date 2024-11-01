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

package com.paddlesandbugs.dahdidahdit.tennis;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.atomic.AtomicReference;

import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.TestingUtils;
import com.paddlesandbugs.dahdidahdit.base.LearningValue;
import com.paddlesandbugs.dahdidahdit.network.MorseTennis;
import com.paddlesandbugs.dahdidahdit.network.mopp.Packet;

public class MorseTennisTest {

    public final StringBuilder sent = new StringBuilder();

    public final StringBuilder printed = new StringBuilder();


    @NonNull
    private static Packet toPacket(String msg) {
        final MorseCode.MutableCharacterList characterData = new MorseCode.MutableCharacterList(msg);
        return new Packet(characterData, 16);
    }


    private static void send(MorseTennis sut, String text) {
        sut.enqueueWord(new MorseCode.MutableCharacterList(text));
    }


    @Test
    public void test() {
        Activity mockContext = TestingUtils.createActivityMock();

        TextView scoreViewMock = Mockito.mock(TextView.class);

        TextView callViewMock = Mockito.mock(TextView.class);

        View stationViewMock = Mockito.mock(View.class);
        Mockito.when(stationViewMock.findViewById(R.id.score)).thenReturn(scoreViewMock);
        Mockito.when(stationViewMock.findViewById(R.id.call)).thenReturn(callViewMock);

        TextView infoViewMock = Mockito.mock(TextView.class);

        View statusViewMock = Mockito.mock(View.class);
        Mockito.when(statusViewMock.findViewById(R.id.score_us)).thenReturn(stationViewMock);
        Mockito.when(statusViewMock.findViewById(R.id.score_dx)).thenReturn(stationViewMock);
        Mockito.when(statusViewMock.findViewById(R.id.info)).thenReturn(infoViewMock);

        AtomicReference<TennisMachine.GameState> gameState = new AtomicReference<>();

        Mockito.when(mockContext.findViewById(R.id.morse_tennis_status)).thenReturn(statusViewMock);

        LearningValue wpm = Mockito.mock(LearningValue.class);
        Mockito.when(wpm.get()).thenReturn(12);

        MorseTennis sut = new MorseTennis(this::sender, this::printer, gameState::set).start(mockContext, wpm);

        send(sut, "cq");
        Assert.assertEquals("", sent.toString());

        send(sut, "mt");
        Assert.assertEquals("", sent.toString());

        send(sut, "de");
        Assert.assertEquals("", sent.toString());

        send(sut, "aa1a");
        Assert.assertEquals("cq mt de aa1a", sent.toString());

        // Invite sent
        clear();

        // bb1b replies
        final String msg = "aa1a de bb1b";
        sut.handleReceived(toPacket(msg));

        // aa1a starts

        send(sut, "bb1b");
        Assert.assertEquals("", sent.toString());

        send(sut, "de");
        Assert.assertEquals("", sent.toString());

        send(sut, "aa1a");
        Assert.assertEquals("bb1b de aa1a", sent.toString());

        clear();

        send(sut, "foo");
        Assert.assertEquals("", sent.toString());

        send(sut, "foo");
        Assert.assertEquals("foo", sent.toString());


    }


    private void printer(String s) {
        printed.append(s);
    }


    private void sender(String s) {
        sent.append(s);
    }


    private void clear() {
        printed.setLength(0);
        sent.setLength(0);
    }
}
