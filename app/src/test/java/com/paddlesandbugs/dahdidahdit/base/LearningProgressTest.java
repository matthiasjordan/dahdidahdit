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
