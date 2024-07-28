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

package com.paddlesandbugs.dahdidahdit.network.mopp;

import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.Utils;

/**
 * Parser for the MOPP message format.
 * <p>
 * See <a href="https://github.com/oe1wkl/Morserino-32/blob/master/Documentation/Protocol%20Description/morse_code_over_packet_protocol.md">the
 * Morserino page</a>
 */
public class MOPPParser {

    private MOPPParser() {
        // Nothing
    }


    public static byte[] toMOPP(Packet packet) {
        BitOutputStream bitStream = new BitOutputStream();

        // Protocol version
        bitStream.append(2, 0b01);

        // Stupid unused sequence number
        bitStream.append(6, 0b011011);

        // WPM
        bitStream.append(6, packet.getWpm());

        MorseCode.CharacterList chars = packet.getCharacters();
        int charCount = chars.asString().length();
        int currentCharNum = 0;
        for (MorseCode.CharacterData charData : chars) {
            currentCharNum += 1;
            for (char sign : charData.getCw().toCharArray()) {
                switch (sign) {
                    case '.': {
                        bitStream.append(2, 0b01);
                        break;
                    }
                    case '-': {
                        bitStream.append(2, 0b10);
                        break;
                    }
                    case ' ': {
                        // Inofficial - usually only one word per UDP packet.
                        bitStream.append(2, 0b11);
                    }
                }
            }

            if (currentCharNum != charCount) {
                // Letter is ended but more letters to come.
                bitStream.append(2, 0b00);
            }
        }
        bitStream.append(2, 0b11);

        return bitStream.toByteArray();
    }


    public static Packet fromMOPP(byte[] bytes) {
        MorseCode.MutableCharacterList res = new MorseCode.MutableCharacterList();

        BitInputStream bis = new BitInputStream(bytes);
        int protocol = bis.getBits(2);
        if (protocol != 1) {
            return new Packet(res, 16);
        }

        int seq = bis.getBits(6); // dont't care
        int wpm = bis.getBits(6);
        final MorseCode instance = MorseCode.getInstance();
        StringBuilder s = new StringBuilder();
        boolean run = true;
        int a;
        while (run && (a = bis.getBits(2)) != -1) {
            switch (a) {
                case 0b01: {
                    s.append('.');
                    break;
                }
                case 0b10: {
                    s.append('-');
                    break;
                }
                case 0b00: {
                    // End of character
                    endOfChar(res, instance, s);
                    break;
                }
                case 0b11: {
                    // End of word and also end of character
                    endOfChar(res, instance, s);
//                    run = false;
                    break;
                }
            }
        }
        endOfChar(res, instance, s);

        return new Packet(res, wpm);
    }


    private static void endOfChar(MorseCode.MutableCharacterList res, MorseCode instance, StringBuilder s) {
        final String morse = s.toString();
        if (!Utils.isEmpty(morse)) {
            MorseCode.CharacterData d = instance.morseToText(morse);
            if (d != null) {
                res.add(d);
            }
            s.setLength(0);
        }
    }
}
