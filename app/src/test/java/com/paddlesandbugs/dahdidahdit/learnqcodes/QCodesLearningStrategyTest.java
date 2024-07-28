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
