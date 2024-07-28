package com.paddlesandbugs.dahdidahdit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.paddlesandbugs.dahdidahdit.copytrainer.CopyTrainer;
import com.paddlesandbugs.dahdidahdit.copytrainer.KochSequence;

public class MorseCodeTest {

    private final MorseCode sut = MorseCode.getInstance();

    private final CopyTrainer trainer = new CopyTrainer(null, new KochSequence(), null);


    @Test
    public void testTextToMorse() {
        assertEquals("hallo", ".... .- .-.. .-.. ---", MorseCode.textToMorse("hallo"));
        assertEquals("cq dx", "-.-. --.-\n-.. -..-", MorseCode.textToMorse("cq dx"));
    }


    @Test
    public void testMakeDisplayString() {
        assertEquals("Aa", sut.get("a").makeDisplayString());
        assertEquals("1", sut.get("1").makeDisplayString());
        assertEquals(".", sut.get(".").makeDisplayString());
        assertEquals("<ve>", sut.get("<ve>").makeDisplayString());
    }


    @Test
    public void testReverse() {
        MorseCode.CharacterList sut = new MorseCode.MutableCharacterList("abc");
        assertEquals(new MorseCode.MutableCharacterList("cba"), sut.reverse());
    }


    @Test
    public void testCharDataEquals() {
        MorseCode.CharacterData c0 = trainer.getSequence().getChar(0).get(0);
        MorseCode.CharacterData c1 = trainer.getSequence().getChar(0).get(1);

        assertEquals("k", MorseCode.getInstance().get("k"), c0);
        assertEquals("m", MorseCode.getInstance().get("m"), c1);
    }


    @Test
    public void testCharacterList1() {
        MorseCode.CharacterList sut = new MorseCode.MutableCharacterList("abc");
        assertEquals(3, sut.size());
        assertEquals("cba", sut.reverse().asString());
    }


    @Test
    public void testCharacterList2a() {
        MorseCode.CharacterList sut = new MorseCode.MutableCharacterList("ab<ka>c");
        assertEquals(4, sut.size());
        assertEquals("c<ka>ba", sut.reverse().asString());
    }


    @Test
    public void testCharacterList2a1() {
        MorseCode.CharacterList sut = new MorseCode.MutableCharacterList("ab<xx>c");
        assertEquals(3, sut.size());
        assertEquals("cba", sut.reverse().asString());
    }


    @Test
    public void testCharacterList2b() {
        MorseCode.CharacterList sut = new MorseCode.MutableCharacterList("ab<kac");
        assertEquals(5, sut.size());
        assertEquals("cakba", sut.reverse().asString());
    }


    @Test
    public void testCharacterList3() {
        MorseCode.CharacterList sut = new MorseCode.MutableCharacterList("ab|c");
        assertEquals(4, sut.size());
        assertEquals("abc", sut.asString());

        MorseCode.CharacterList expected = new MorseCode.MutableCharacterList();
        expected.add(MorseCode.getInstance().get("a"));
        expected.add(MorseCode.getInstance().get("b"));
        expected.add(MorseCode.SYLLABLEBREAK);
        expected.add(MorseCode.getInstance().get("c"));
        assertEquals(expected, sut);
    }


    @Test
    public void testCharacterListCountRawChars() {
        assertEquals(3, MorseCode.countRawChars("abc"));
        assertEquals(7, MorseCode.countRawChars("abc def  \r\n\t\t\r g"));
        assertEquals(6, MorseCode.countRawChars("<kn><as><ar>a\r\r\n\naa"));
    }


    @Test
    public void testExplode() {
        MorseCode.ExplodedCharacterList res = new MorseCode.MutableCharacterList("paris").explode();
        final MorseCode.MutableCharacterList expected = new MorseCode.MutableCharacterList();
        expected.add(MorseCode.getInstance().get("p"));
        expected.add(MorseCode.CHARBREAK);
        expected.add(MorseCode.getInstance().get("a"));
        expected.add(MorseCode.CHARBREAK);
        expected.add(MorseCode.getInstance().get("r"));
        expected.add(MorseCode.CHARBREAK);
        expected.add(MorseCode.getInstance().get("i"));
        expected.add(MorseCode.CHARBREAK);
        expected.add(MorseCode.getInstance().get("s"));
        assertEquals(expected, res);
    }

}