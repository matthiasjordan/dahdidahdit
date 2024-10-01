package com.paddlesandbugs.dahdidahdit.text;

import org.junit.Assert;
import org.junit.Test;

import com.paddlesandbugs.dahdidahdit.MorseCode;

/**
 * Tests {@link QSOTextGenerator}.
 */
public class QSOTextGeneratorTest extends AbstractTextGeneratorTest {

    private static final int RUNS = 10;


    @Test
    public void testAPI() {
        for (int i = 0; (i < RUNS); i++) {
            QSOTextGenerator sut = new QSOTextGenerator();

            MorseCode.CharacterList actual = TextTestUtils.read(sut, 400);

            String actualStr = actual.asString();
            System.out.println("qso: " + actualStr);

            Assert.assertFalse("%", actualStr.contains("%"));
            Assert.assertTrue(actualStr.length() > 30);
        }
    }


    @Test
    public void testInternal() {
        for (int i = 0; (i < RUNS); i++) {
            QSOTextGenerator sut = new QSOTextGenerator();

            String actualStr = sut.createRandomQSO();
            System.out.println("qso: " + actualStr);

            Assert.assertFalse("%", actualStr.contains("%"));
            Assert.assertTrue(actualStr.length() > 30);
        }
    }

}
