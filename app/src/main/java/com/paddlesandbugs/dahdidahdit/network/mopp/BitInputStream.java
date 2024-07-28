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

import java.util.ArrayList;

public class BitInputStream {
    private final ArrayList<Byte> bytes;

    private int currentBytePos;

    private byte currentByte;

    private boolean closed;

    /**
     * Range from 1..8.
     */
    private int currentBitPos = 1;


    public BitInputStream(byte[] bytes) {
        ArrayList<Byte> bb = new ArrayList<>(bytes.length);
        for (byte b : bytes) {
            bb.add(b);
        }
        this.bytes = bb;
        currentByte = -1;
        currentBytePos = -1;
    }


    public int getBitsRightAligned(int count) {
        int res = 0;
        for (int i = 0; (i < count); i++) {
            int bit = getBit();
            if (bit != -1) {
                res = (res << 1) | bit;
            }
        }

        return res;
    }


    public int getBits(int count) {
        int res = -1;

        for (int i = 0; (i < count); i++) {
            final int bit = getBit();
            if (bit == -1) {
                break;
            }
            if (res == -1) {
                res = 0;
            }
            res = (res << 1) | bit;
        }

        return res;
    }


    public int getBit() {
        int bit = -1;
        if (!closed && ((currentBytePos == -1) || (currentBitPos > 8))) {
            currentBytePos += 1;
            currentBitPos = 1;
            if (bytes.size() > currentBytePos) {
                currentByte = bytes.get(currentBytePos);
            } else {
                closed = true;
            }
        }

        if (!closed) {
            int bitmask = 1 << (8 - currentBitPos);
            bit = (currentByte & bitmask) == 0 ? 0 : 1;
            currentBitPos += 1;
        }
        return bit;
    }

}
