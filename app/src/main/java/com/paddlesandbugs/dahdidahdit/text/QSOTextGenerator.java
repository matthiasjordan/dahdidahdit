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

package com.paddlesandbugs.dahdidahdit.text;

import android.content.Context;

import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.Utils;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * Generates rubber-stamp QSOs.
 */
public class QSOTextGenerator extends AbstractTextGenerator {


    private static final int WORDBREAKS_AFTER_PROSIGN = 2;

    private final List<String> templates = Arrays.asList( //
            "%cq% de %decalls% <ar>  " //
                    + "%decall% de %tocall% <kn>  " //
                    + "%tocall% de %decall% %daytime% es %tkscall% = ur rst %torsts% = my name %denames% = qth %deqth% = hw cpi? %tocall% de %decall% <kn>  " //
                    + "%decall% de %tocall% %totksrprt% = ur rst %dersts% = my name %tonames% = qth %toqth% %decall% de %tocall% <kn>  " //
                    + "%tocall% de %decall% %der% = %detksrprt% = ant %deant% = tx %detx% = %dewx% = will qsl via buro = pls ur qsl = tks qso 73 es %debye% = %tocall% de %decall% k  " //
                    + "%decall% de %tocall% %tor% = tx %totx% ant %toant% = my qsl ok via buro = 73 es tks fer qso %tobye% %dename% %decall% de %tocall% sk ", //

            "%decalls% test   " //
                    + "%tocall% k  " //
                    + "%tocall% 599 %decounter% k  " //
                    + "%decall% 599 %tocounter%  " //
                    + "tu "

    );


    private final CallsignGenerator callsigns;

    private final Random random = new Random();

    private final MorseCode.CharacterList qso;

    private final Map<String, String> values = new HashMap<>();

    private int pauses = 0;


    public QSOTextGenerator(Context context) {
        callsigns = new CallsignGenerator(context, MainActivity.stopwords);

        fillValues();

        final String ready = createRandomQSO();

        qso = new MorseCode.MutableCharacterList(ready);
    }


    String createRandomQSO() {
        int tempN = random.nextInt(templates.size());
        String template = templates.get(tempN);
        final String ready = replace(template);
        return ready;
    }


    private void fillValues() {
        final Op op = op();
        Op dxop;
        do {
            dxop = op();
        } while (op.equals(dxop));

        setValue("cq", cq());

        setValue("decounter", String.valueOf(random.nextInt(100)));
        setValue("tocounter", String.valueOf(random.nextInt(100)));

        setCall("decall", call());
        setCall("tocall", call());

        setValue("daytime", oneOf("gm", "ge", "gd"));
        setValue("tkscall", oneOf("tks", "tnx") + " " + oneOf("fer call", " call", " fer nice call"));
        setMulti("torst", rst(), 2);
        setMulti("dename", op.name, 2);
        setValue("deqth", Utils.repeat(op.qth, randomInt(1, 2)));

        setValue("totksrprt", tksrprt());
        setMulti("derst", rst(), 2);
        setMulti("toname", dxop.name, 2);
        setValue("toqth", Utils.repeat(dxop.qth, randomInt(1, 2)));

        setValue("der", Utils.repeat("r", randomInt(1, 3)));
        setValue("detksrprt", tksrprt());
        setValue("deant", ant());
        setValue("detx", tx());
        setValue("dewx", wx());
        setValue("debye", oneOf("cuagn", "cu"));

        setValue("tor", Utils.repeat("r", randomInt(1, 3)));
        setValue("toant", ant());
        setValue("totx", tx());
        setValue("tobye", oneOf("cuagn", "cu"));
    }


    private String call() {
        return callsigns.generateNextWord().asString();
    }


    private String wx() {
        String mod = oneOf("", "light ", "heavy ");
        final String w = oneOf(mod + "thunderstorm", mod + " rain", "sunshine", "cloudy");
        return oneOf("", "wx " + w);
    }


    private String tx() {
        String w = oneOf("1", "2", "5", "10", "15", "25", "50", "100", "1000", "1500");
        return oneOf("ft818", "ft891", "ft10dx", "ft-991", "ft-dx3000", "ts-890s", "ts-990s", "ic7300", "ic705", "ic9700", w + " w");
    }


    private String ant() {
        int m = randomInt(2, 15);
        return oneOf("dipole", "inv v", "yagi", "zepp", "efhw") + " at " + m + " m";
    }


    private String tksrprt() {
        return oneOf("tks rprt", "tks fer rprt", "tks fer nice rprt", "tks fer ur rprt");
    }

    private Op op() {
        return oneOf( //
                Op.as("peter", "london"), //
                Op.as("alan", "london"), //
                Op.as("ray", "paradise valley"), //
                Op.as("mat", "essen"), //
                Op.as("jacque", "paris"), //
                Op.as("klaus", "berlin"), //
                Op.as("nobuaki", "tokyo"), //
                Op.as("adam", "new york"), //
                Op.as("danny", "washington"), //
                Op.as("lawrence", "los angeles"), //
                Op.as("pierre", "brussels"), //
                Op.as("frans", "amsterdam"), //
                Op.as("eric", "bordeaux"), //

                Op.as("linda", "london"), //
                Op.as("betty-jane", "paradise valley"), //
                Op.as("carmen", "essen"), //
                Op.as("serife", "istanbul"), //
                Op.as("stephanie", "paris"), //
                Op.as("petra", "berlin"), //
                Op.as("ayumi", "tokyo"), //
                Op.as("liz", "new york"), //
                Op.as("tara", "washington"), //
                Op.as("barbara", "los angeles"), //
                Op.as("maxime", "brussels"), //
                Op.as("tine", "amsterdam"),
                Op.as("melissa", "bordeaux") //
        );
    }


    private String rst() {
        int r = randomInt(1, 5);
        int s = randomInt(1, 9);
        int t = randomInt(1, 9);
        return String.valueOf(r) + String.valueOf(s) + String.valueOf(t);
    }


    private void setValue(String name, String value) {
        values.put("%" + name + "%", value);
    }


    private void setValue(String name, String value, String plural) {
        setValue(name, value);
        setValue(name + "s", plural);
    }


    private void setMulti(String what, String singular, int max) {
        int count = randomInt(1, max);
        setValue(what, singular, Utils.repeat(singular, " ", count));
    }


    private void setCall(String callname, String call) {
        setValue(callname, call);
        final String decalls = Utils.repeat(call, randomInt(1, 3));
        setValue(callname + "s", decalls);
    }


    private String cq() {
        int c = randomInt(1, 3);
        return Utils.repeat("cq", c);
    }


    private <T> T oneOf(T... choices) {
        int i = random.nextInt(choices.length);
        return choices[i];
    }


    private int randomInt(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }


    private String replace(String t) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            t = t.replace(entry.getKey(), entry.getValue());
        }
        return t;
    }


    @Override
    public int getTextID() {
        return R.string.text_generator_qsos;
    }


    @Override
    public boolean hasNext() {
        return !isClosed() && (qso.size() != 0);
    }


    @Override
    public TextPart next() {
        if (pauses > 0) {
            pauses -= 1;
            return new TextPart(MorseCode.WORDBREAK);
        }

        MorseCode.CharacterData cd = qso.pop();

        if (cd == MorseCode.WORDBREAK) {
            while ((qso.size() != 0) && (qso.get(0) == MorseCode.WORDBREAK)) {
                // Skip wordbreaks
                qso.pop();
            }
        } else if (cd.is(MorseCode.PROSIGN)) {
            this.pauses = WORDBREAKS_AFTER_PROSIGN;
        }

        return new TextPart(cd);
    }

    private static class Op {
        private String name;
        private String qth;

        public static Op as(String name, String qth) {
            final Op op = new Op();
            op.name = name;
            op.qth = qth;
            return op;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Op op = (Op) o;
            return Objects.equals(name, op.name) && Objects.equals(qth, op.qth);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, qth);
        }
    }
}
