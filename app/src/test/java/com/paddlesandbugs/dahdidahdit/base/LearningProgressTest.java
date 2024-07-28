package com.paddlesandbugs.dahdidahdit.base;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LearningProgressTest {

    private String history = "";
    private LearningProgress sut;


    @Before
    public void setup() {
        sut = new LearningProgress(null, null, 10, 5) {

            @Override
            protected String readHistory() {
                return history;
            }


            @Override
            protected void writeHistory(String h) {
                history = h;
            }
        };
    }


    @Test
    public void test0() {
        Assert.assertEquals(getMap(), sut.countMistakes());
    }


    @Test
    public void test1a() {
        sut.update(LearningProgress.Mistake.HIGH);
        Assert.assertEquals(getMap(0, 0, 1), sut.countMistakes());
    }


    @Test
    public void test1b() {
        sut.update(LearningProgress.Mistake.HIGH);
        sut.update(LearningProgress.Mistake.HIGH);
        Assert.assertEquals(getMap(0, 0, 2), sut.countMistakes());
    }


    @Test
    public void test6() {
        sut.update(LearningProgress.Mistake.HIGH);
        sut.update(LearningProgress.Mistake.LOW);
        sut.update(LearningProgress.Mistake.MEDIUM);
        sut.update(LearningProgress.Mistake.MEDIUM);
        sut.update(LearningProgress.Mistake.HIGH);
        sut.update(LearningProgress.Mistake.HIGH);
        Assert.assertEquals(getMap(1, 2, 3), sut.countMistakes());
    }


    @Test
    public void test7() {
        sut.update(LearningProgress.Mistake.HIGH);
        sut.update(LearningProgress.Mistake.LOW);
        sut.update(LearningProgress.Mistake.MEDIUM);
        sut.update(LearningProgress.Mistake.MEDIUM);
        sut.update(LearningProgress.Mistake.HIGH);
        sut.update(LearningProgress.Mistake.HIGH);
        sut.update(LearningProgress.Mistake.HIGH);
        Assert.assertEquals(getMap(1, 2, 4), sut.countMistakes());
    }


    @Test
    public void test7a() {
        sut.update(LearningProgress.Mistake.HIGH);
        sut.update(LearningProgress.Mistake.LOW);
        sut.update(LearningProgress.Mistake.MEDIUM);
        sut.update(LearningProgress.Mistake.MEDIUM);
        sut.update(LearningProgress.Mistake.HIGH);
        sut.update(LearningProgress.Mistake.HIGH);
        sut.update(LearningProgress.Mistake.HIGH);
        Assert.assertEquals(getMap(0, 2, 3), sut.countRecentMistakes());
    }


    @Test
    public void test7b() {
        sut.update(LearningProgress.Mistake.HIGH);
        sut.update(LearningProgress.Mistake.LOW);
        sut.update(LearningProgress.Mistake.MEDIUM);
        sut.update(LearningProgress.Mistake.MEDIUM);
        sut.markRecents();
        sut.update(LearningProgress.Mistake.HIGH);
        sut.update(LearningProgress.Mistake.HIGH);
        sut.update(LearningProgress.Mistake.HIGH);
        Assert.assertEquals(getMap(1, 2, 4), sut.countMistakes());
        Assert.assertEquals(getMap(0, 0, 3), sut.countRecentMistakes());
    }


    @Test
    public void test7c() {
        sut.update(LearningProgress.Mistake.HIGH); // pushed out
        sut.update(LearningProgress.Mistake.LOW);
        sut.update(LearningProgress.Mistake.LOW);
        sut.update(LearningProgress.Mistake.LOW);
        sut.update(LearningProgress.Mistake.LOW);
        sut.update(LearningProgress.Mistake.LOW);
        sut.update(LearningProgress.Mistake.MEDIUM);
        sut.update(LearningProgress.Mistake.MEDIUM);
        sut.markRecents();
        sut.update(LearningProgress.Mistake.HIGH);
        sut.update(LearningProgress.Mistake.HIGH);
        sut.update(LearningProgress.Mistake.HIGH);
        Assert.assertEquals(getMap(5, 2, 3), sut.countMistakes());
        Assert.assertEquals(getMap(0, 0, 3), sut.countRecentMistakes());
    }


    private LearningProgress.MistakeMap getMap() {
        return getMap(0, 0, 0);
    }


    private LearningProgress.MistakeMap getMap(int l, int m, int h) {
        LearningProgress.MistakeMap map = new LearningProgress.MistakeMap();
        map.put(LearningProgress.Mistake.LOW, l);
        map.put(LearningProgress.Mistake.MEDIUM, m);
        map.put(LearningProgress.Mistake.HIGH, h);
        return map;
    }

}
