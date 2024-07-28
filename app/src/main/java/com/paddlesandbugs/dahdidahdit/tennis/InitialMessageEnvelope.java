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

package com.paddlesandbugs.dahdidahdit.tennis;

/**
 * Encoding and decoding of a proposed in-band header for initial messages.
 */
class InitialMessageEnvelope {

    private static final short PROTOCOL_VERSION = 1;

    private static final char INITIAL_MESSAGE_MARKER = '°';

    private static final int INITIAL_MESSAGE_HEADER_LENGTH = 5;

    private short protocolVersion = PROTOCOL_VERSION;

    private final InitialMessageData d = new InitialMessageData();

    private String text;


    public static InitialMessageEnvelope parseInitial(String message) {
        InitialMessageEnvelope msg = new InitialMessageEnvelope();
        char[] m = message.toCharArray();
        if (m.length == 0) {
            msg.text = "";
            return msg;
        }

        char marker = m[0];
        if (marker == INITIAL_MESSAGE_MARKER) {
            if (m.length < INITIAL_MESSAGE_HEADER_LENGTH) {
                msg.text = "";
                return msg;
            }

            msg.protocolVersion = (short) (((short) m[1]) - 32);
            msg.d.msgSet = (short) (((short) m[2]) - 32);
            msg.d.scoring = (short) (((short) m[3]) - 32);
            char reserved = m[4];
            msg.text = message.substring(INITIAL_MESSAGE_HEADER_LENGTH);
        } else {
            msg.text = message;
        }
        return msg;
    }


    private static String encodeInitial(InitialMessageEnvelope msg) {
        String message;

        char[] buf = new char[INITIAL_MESSAGE_HEADER_LENGTH];
        buf[0] = INITIAL_MESSAGE_MARKER; // Marks initial message
        buf[1] = (char) ((char) msg.protocolVersion + 32);
        buf[2] = (char) (msg.d.msgSet + 32);
        buf[3] = (char) (msg.d.scoring + 32);
        buf[4] = 0; // reserved
        String prefix = new String(buf);
        message = prefix + msg.text;
        return message;
    }


    public static class InitialMessageData {
        short msgSet;

        short scoring;
    }

}
