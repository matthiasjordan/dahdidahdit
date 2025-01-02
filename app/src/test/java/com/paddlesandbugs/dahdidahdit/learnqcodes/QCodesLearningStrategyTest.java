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

package com.paddlesandbugs.dahdidahdit.learnqcodes;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.TimeUnit;

import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.base.LearningStrategy;
import com.paddlesandbugs.dahdidahdit.sound.MorsePlayer;

public class QCodesLearningStrategyTest {

    private boolean newCardCountUpdated;
    private boolean newDayHandled;


    @Before
    public void setup() {
        newCardCountUpdated = false;
        newDayHandled = false;
    }


    @Test
    public void test0() {
        QCodesLearningStrategy.FactProvider fp = Mockito.mock(QCodesLearningStrategy.FactProvider.class);
        Mockito.when(fp.nextOnHand()).thenReturn(null);
        QCodesLearningStrategy sut = new QCodesLearningStrategy(null, fp, null) {
            @Override
            long getTimestamp() {
                return TimeUnit.MINUTES.toMillis(100);
            }


            @Override
            void handleNewDay(long now) {
            }
        };

        LearningStrategy.SessionConfig res = sut.getSessionConfig();

        Assert.assertTrue("res is null", res == null);
    }


    @Test
    public void test1() {
        Fact f = new Fact(1, new MorseCode.MutableCharacterList("qsq"), "doc on board");

        QCodesLearningStrategy.FactProvider fp = Mockito.mock(QCodesLearningStrategy.FactProvider.class);
        Mockito.when(fp.nextOnHand()).thenReturn(f);
        QCodesLearningStrategy sut = new QCodesLearningStrategy(null, fp, null) {


            @Override
            long getTimestamp() {
                return TimeUnit.MINUTES.toMillis(100);
            }


            @Override
            long getNow() {
                return TimeUnit.MINUTES.toMillis(101);
            }


            @Override
            public SessionConfig getSessionConfig(Fact f) {
                return new SessionConfig(new MorsePlayer.Config());
            }
        };

        LearningStrategy.SessionConfig res = sut.getSessionConfig();

        Assert.assertNotNull("has next session", res);
        Assert.assertTrue("misc is a Fact", res.misc instanceof Fact);
        //Assert.assertTrue("updated", newCardCountUpdated);
    }

}
