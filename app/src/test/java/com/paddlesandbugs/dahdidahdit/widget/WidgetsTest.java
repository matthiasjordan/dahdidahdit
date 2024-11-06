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

package com.paddlesandbugs.dahdidahdit.widget;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Tests for {@link Widgets}.
 */
public class WidgetsTest {

    @Test
    public void test0() {
        Widgets.PracticeData data = new Widgets.PracticeData();

        data.nowPracticedMs = 1_000_000;
        Widgets.updatePracticed(data);

        Assert.assertEquals("daysInARow", 1, data.daysInARow);
        Assert.assertEquals("maxDaysInARow", 1, data.maxDaysInARow);
        Assert.assertEquals("lastPracticedMs", data.nowPracticedMs, data.lastPracticedMs);
    }


    @Test
    public void test1() {
        Widgets.PracticeData data = new Widgets.PracticeData();

        data.nowPracticedMs = 1_000_000;
        Widgets.updatePracticed(data);

        data.nowPracticedMs = 1_000_000 + TimeUnit.DAYS.toMillis(1);
        Widgets.updatePracticed(data);

        Assert.assertEquals("daysInARow", 2, data.daysInARow);
        Assert.assertEquals("maxDaysInARow", 2, data.maxDaysInARow);
        Assert.assertEquals("lastPracticedMs", data.nowPracticedMs, data.lastPracticedMs);
    }


    @Test
    public void test2() {
        Widgets.PracticeData data = new Widgets.PracticeData();

        data.nowPracticedMs = 1_000_000;
        Widgets.updatePracticed(data);

        data.nowPracticedMs = 1_000_000 + TimeUnit.DAYS.toMillis(1);
        Widgets.updatePracticed(data);

        data.nowPracticedMs = 1_000_000 + TimeUnit.DAYS.toMillis(2);
        Widgets.updatePracticed(data);

        Assert.assertEquals("daysInARow", 3, data.daysInARow);
        Assert.assertEquals("maxDaysInARow", 3, data.maxDaysInARow);
        Assert.assertEquals("lastPracticedMs", data.nowPracticedMs, data.lastPracticedMs);
    }


    @Test
    public void test3() {
        Widgets.PracticeData data = new Widgets.PracticeData();

        data.nowPracticedMs = 1_000_000;
        Widgets.updatePracticed(data);

        data.nowPracticedMs = 1_000_000 + TimeUnit.DAYS.toMillis(1);
        Widgets.updatePracticed(data);

        data.nowPracticedMs = 1_000_000 + TimeUnit.DAYS.toMillis(2);
        Widgets.updatePracticed(data);

        data.nowPracticedMs = 1_000_000 + TimeUnit.DAYS.toMillis(3);
        //Widgets.updatePracticed(data);

        data.nowPracticedMs = 1_000_000 + TimeUnit.DAYS.toMillis(4);
        Widgets.updatePracticed(data);

        Assert.assertEquals("daysInARow", 1, data.daysInARow);
        Assert.assertEquals("maxDaysInARow", 3, data.maxDaysInARow);
        Assert.assertEquals("lastPracticedMs", data.nowPracticedMs, data.lastPracticedMs);
    }


    @Test
    public void test4() {
        Widgets.PracticeData data = new Widgets.PracticeData();

        data.nowPracticedMs = 1_000_000;
        Widgets.updatePracticed(data);

        data.nowPracticedMs = 1_000_000 + TimeUnit.DAYS.toMillis(1);
        Widgets.updatePracticed(data);

        data.nowPracticedMs = 1_000_000 + TimeUnit.DAYS.toMillis(2);
        Widgets.updatePracticed(data);

        data.nowPracticedMs = 1_000_000 + TimeUnit.DAYS.toMillis(3);
        //Widgets.updatePracticed(data);

        data.nowPracticedMs = 1_000_000 + TimeUnit.DAYS.toMillis(4);
        Widgets.updatePracticed(data);

        data.nowPracticedMs = 1_000_000 + TimeUnit.DAYS.toMillis(5);
        Widgets.updatePracticed(data);

        Assert.assertEquals("daysInARow", 2, data.daysInARow);
        Assert.assertEquals("maxDaysInARow", 3, data.maxDaysInARow);
        Assert.assertEquals("lastPracticedMs", data.nowPracticedMs, data.lastPracticedMs);
    }


    @Test
    public void test5() {
        Widgets.PracticeData data = new Widgets.PracticeData();

        data.nowPracticedMs = 1_000_000;
        Widgets.updatePracticed(data);

        data.nowPracticedMs = 1_000_000 + TimeUnit.DAYS.toMillis(1);
        Widgets.updatePracticed(data);

        data.nowPracticedMs = 1_000_000 + TimeUnit.DAYS.toMillis(2);
        Widgets.updatePracticed(data);

        data.nowPracticedMs = 1_000_000 + TimeUnit.DAYS.toMillis(3);
        //Widgets.updatePracticed(data);

        data.nowPracticedMs = 1_000_000 + TimeUnit.DAYS.toMillis(4);
        Widgets.updatePracticed(data);

        data.nowPracticedMs = 1_000_000 + TimeUnit.DAYS.toMillis(5);
        Widgets.updatePracticed(data);

        data.nowPracticedMs = 1_000_000 + TimeUnit.DAYS.toMillis(6);
        Widgets.updatePracticed(data);

        data.nowPracticedMs = 1_000_000 + TimeUnit.DAYS.toMillis(7);
        Widgets.updatePracticed(data);

        Assert.assertEquals("daysInARow", 4, data.daysInARow);
        Assert.assertEquals("maxDaysInARow", 4, data.maxDaysInARow);
        Assert.assertEquals("lastPracticedMs", data.nowPracticedMs, data.lastPracticedMs);
    }


    @Test
    public void testResetDaysInARow() {
        Widgets.PracticeData data = new Widgets.PracticeData();
        data.nowPracticedMs = 1639350000019L;
        data.lastPracticedMs = 1638734189475L;
        data.daysInARow = 4;
        data.maxDaysInARow = 4;

        Widgets.resetDaysInARow(data);

        Assert.assertEquals("",0, data.daysInARow);
        Assert.assertEquals("",4, data.maxDaysInARow);
    }


//2021-12-13 00:00:00.019 3667-3667/com.paddlesandbugs.dahdidahdit I/WIDGET: data  after: PracticeData{nowPracticedMs=1639350000019, lastPracticedMs=1638734189475, daysInARow=4, maxDaysInARow=4}
//2021-12-14 00:00:00.040 3667-3667/com.paddlesandbugs.dahdidahdit I/WIDGET: data before: PracticeData{nowPracticedMs=1639436400040, lastPracticedMs=1638734189475, daysInARow=4, maxDaysInARow=4}

}
