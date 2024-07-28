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

package com.paddlesandbugs.dahdidahdit.sound;

public class AtmosphereModel {

    public static SampleGenerator getQRM(int level) {
        switch (level) {
            default: {
                return new StaticSampleGenerator((short) 0);
            }
            case 2: {
                return new QRMSampleGenerator(0.05f);
            }
            case 3: {
                return new QRMSampleGenerator(0.1f);
            }
            case 4: {
                return new QRMSampleGenerator(0.2f);
            }
            case 5: {
                return new QRMSampleGenerator(0.3f);
            }
        }
    }


    public static SampleGenerator getQRN(int level) {
        switch (level) {
            default: {
                return new StaticSampleGenerator((short) 0);
            }
            case 2: {
                return new QRNSampleGenerator(0.2f, 40, 40);
            }
            case 3: {
                return new QRNSampleGenerator(0.4f, 20, 100);
            }
            case 4: {
                return new QRNSampleGenerator(0.6f, 10, 100);
            }
            case 5: {
                return new QRNSampleGenerator(0.8f, 5, 100);
            }
        }
    }


    public static SampleGenerator getQSB(int level) {
        switch (level) {
            default: {
                return new StaticSampleGenerator(Short.MAX_VALUE);
            }
            case 2: {
                return new QSBGenerator(0.7f, 1.0f, 20, 40);
            }
            case 3: {
                return new QSBGenerator(0.6f, 1.0f, 20, 30);
            }
            case 4: {
                return new QSBGenerator(0.4f, 1.0f, 20, 30);
            }
            case 5: {
                return new QSBGenerator(0.2f, 1.0f, 20, 20);
            }
        }
    }

}
