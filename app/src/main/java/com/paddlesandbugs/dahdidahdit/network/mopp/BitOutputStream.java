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

import java.io.ByteArrayOutputStream;

public class BitOutputStream {

    private byte currentByte = 0x0;

    private byte currentBitCount = 0;

    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();


    public BitOutputStream() {
    }


    private void flush() {
        if (currentBitCount != 0) {
            baos.write(currentByte);
        }
        currentByte = 0x0;
        currentBitCount = 0;
    }


    /**
     * Appends the LSB of the given int to the currentByte.
     *
     * @param bit the bit to append - only the LSB is of importance
     */
    public void append(int bit) {
        final int bitsInAByte = 8;
        final int targetBit = bitsInAByte - currentBitCount;
        final int bitShifted = bit << targetBit - 1;
        currentByte |= bitShifted;
        currentBitCount += 1;
        if (currentBitCount == bitsInAByte) {
            flush();
        }
    }


    public void append(int bitCount, int bits) {
        int bitmask = 1 << bitCount - 1;

        for (int bit = bitCount; (bit > 0); bit--) {
            int b = (bits & bitmask) == 0 ? 0 : 1;
            append(b);
            bits <<= 1;
        }

    }


    public byte[] toByteArray() {
        flush();
        return baos.toByteArray();
    }
}
