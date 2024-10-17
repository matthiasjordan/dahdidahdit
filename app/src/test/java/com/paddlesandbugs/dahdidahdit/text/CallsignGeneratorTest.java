package com.paddlesandbugs.dahdidahdit.text;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.paddlesandbugs.dahdidahdit.Distribution;
import com.paddlesandbugs.dahdidahdit.MorseCode;

import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.res.Resources;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.Set;

public class CallsignGeneratorTest extends AbstractTextGeneratorTest {

    private static final Stopwords stopwords = new Stopwords();

    private Context context;

    @Before
    public void setup() {
        context = Mockito.mock(Context.class);
        Resources resources = Mockito.mock(Resources.class);
        Mockito.when(context.getResources()).thenReturn(resources);
        Mockito.when(resources.openRawResource(Mockito.anyInt())).then(new Answer<ByteArrayInputStream>() {

            @Override
            public ByteArrayInputStream answer(InvocationOnMock invocation) throws Throwable {
                return new ByteArrayInputStream("ab\nba-bc\n".getBytes());
            }
        });
    }

    @Test
    public void testGenerate1() {
        CallsignGenerator sut = new CallsignGenerator(context, stopwords);

        MorseCode.CharacterList res = TextTestUtils.read(sut, 3);
        System.out.println(res);

        assertTrue(res.size() >= 3);
        assertTrue(res.size() <= 6);
    }

    @Test
    public void testGenerate2() {
        CallsignGenerator sut = new CallsignGenerator(context, stopwords);

        MorseCode.CharacterList res = TextTestUtils.read(sut, 7);
        System.out.println(res);

        assertTrue(res.size() >= 7);
        assertTrue(res.size() <= 13);
    }

    @Test
    public void testGenerateAllowed1() {
        Set<MorseCode.CharacterData> allowed = MorseCode.asSet("abc12");
        CallsignGenerator sut = new CallsignGenerator(context, stopwords, allowed);

        MorseCode.CharacterList res = TextTestUtils.read(sut, 100);

        Set<MorseCode.CharacterData> found = new HashSet<>();
        for (MorseCode.CharacterData c : res) {
            found.add(c);
        }

        found.remove(MorseCode.WORDBREAK);

        Assert.assertEquals(allowed, found);
    }

    @Test
    public void testDistribution() {
        Distribution<String> d = CallsignGenerator.generatePrefixDistribution(context);
        System.out.println(d);

    }
}