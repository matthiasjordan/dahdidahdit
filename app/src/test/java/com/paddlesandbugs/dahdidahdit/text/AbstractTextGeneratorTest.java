package com.paddlesandbugs.dahdidahdit.text;

import com.paddlesandbugs.dahdidahdit.MorseCode;

public abstract class AbstractTextGeneratorTest {

    protected static MorseCode.CharacterList read(TextGenerator gen, int count) {
        MorseCode.CharacterList res = new MorseCode.MutableCharacterList();
        int i = 0;
        while (gen.hasNext()) {
            res.add(gen.next().getChar());

            if (++i == count) {
                gen.close();
            }
        }
        return res;
    }


    protected static MorseCode.CharacterList readPrinted(TextGenerator gen, int count) {
        MorseCode.CharacterList res = new MorseCode.MutableCharacterList();
        int i = 0;
        while (gen.hasNext()) {
            final TextGenerator.TextPart textPart = gen.next();
            if (textPart.isPrinted()) {
                res.add(textPart.getChar());
            }

            if (++i == count) {
                gen.close();
            }
        }
        return res;
    }

}