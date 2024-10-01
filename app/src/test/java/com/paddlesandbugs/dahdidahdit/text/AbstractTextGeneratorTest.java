package com.paddlesandbugs.dahdidahdit.text;

import androidx.annotation.NonNull;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public abstract class AbstractTextGeneratorTest {

    @NonNull
    protected static ByteArrayInputStream fakeRawResource(String x) {
        return new ByteArrayInputStream(x.getBytes(StandardCharsets.UTF_8));
    }

}