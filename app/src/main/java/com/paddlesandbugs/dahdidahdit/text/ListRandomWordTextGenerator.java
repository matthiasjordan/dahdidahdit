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

package com.paddlesandbugs.dahdidahdit.text;

import android.content.Context;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.Utils;

/**
 * Generates words from the word list file.
 */
public class ListRandomWordTextGenerator extends RandomWordTextGenerator {


    /**
     * The resource ID of the word list raw resource.
     */
    public static final List<Integer> WORDLIST_RSRCS = Arrays.asList(R.raw.headcopy_words_manual, R.raw.headcopy_words_2000);


    /**
     * Will read all words from the known word lists.
     *
     * @param context   the context
     * @param stopwords the stopwords
     */
    public ListRandomWordTextGenerator(Context context, Stopwords stopwords) {
        this(context, stopwords, Integer.MAX_VALUE);
    }


    /**
     * Will read at most count words from the known word lists.
     *
     * @param context   the context
     * @param stopwords the stopwords
     * @param count     the number of words to read at most from the internal lists
     */
    public ListRandomWordTextGenerator(Context context, Stopwords stopwords, int count) {
        super(stopwords);
        if (count < 1) {
            count = Integer.MAX_VALUE;
        }

        for (int rsrc : WORDLIST_RSRCS) {
            int linesToRead = count - size();

            if (linesToRead <= 0) {
                break;
            }

            try {
                Stream<String> lines = Utils.toStream(context, rsrc);
                lines.limit(linesToRead).forEach(this::add);
            } catch (IOException e) {
                // Nothing
            }
        }
    }


    /**
     * Will read all words from the given stream.
     *
     * @param context   the context
     * @param stopwords the stopwords
     * @param words     the stream of words to choose random words from
     */
    public ListRandomWordTextGenerator(Context context, Stopwords stopwords, Stream<String> words) {
        super(stopwords);
        words.forEach(this::add);
    }


    /**
     * Will read all words from the given stream and keep up to the given maximum of words for random selection.
     *
     * @param context   the context
     * @param stopwords the stopwords
     * @param words     the stream of words to choose random words from
     * @param max       the maximum number of words to keep
     * @param approxSize the approximate size of the stream
     */
    public ListRandomWordTextGenerator(Context context, Stopwords stopwords, Stream<String> words, int max, int approxSize) {
        super(stopwords);
        if (max == 0) {
            return;
        }

        Random random = new Random();

        List<String> list = getList();
        AtomicInteger listFill = new AtomicInteger(); // Up to where are the initial words
        Iterator<String> it = words.iterator();
        while (it.hasNext()) {
            final String word = it.next();
            if (list.size() < max) {
                list.add(word);
                listFill.incrementAndGet();
            }
            else {
                if (random.nextInt(approxSize) <= max) {
                    // Select word into the list
                    int removePos = random.nextInt(listFill.get());
                    list.remove(removePos);
                    list.add(word);
                    listFill.decrementAndGet();
                    if (listFill.get() == 0) {
                        break;
                    }
                }
            }
        }
    }


}