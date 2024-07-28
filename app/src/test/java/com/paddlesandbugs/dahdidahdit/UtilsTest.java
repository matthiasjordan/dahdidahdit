package com.paddlesandbugs.dahdidahdit;

import android.content.Context;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.paddlesandbugs.dahdidahdit.text.Stopwords;

public class UtilsTest {

    @Test
    public void testStream() throws IOException {
        Context context = TestingUtils.createContextMock();
        Stopwords stopwords = Mockito.mock(Stopwords.class);

        Stream<String> str = Utils.toStream(context, R.raw.headcopy_words_manual);

        List<String> lines = new ArrayList<>();
        str.forEach(lines::add);
        Assert.assertEquals(Arrays.asList("hallo1","foo1","bar1","baz1","radio1","test1"), lines);
    }

}
