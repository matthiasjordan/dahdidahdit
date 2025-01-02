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

package com.paddlesandbugs.dahdidahdit.settings;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.paddlesandbugs.dahdidahdit.Utils;

public class ReceivedFile {

    private final Context context;

    private final String fileName;


    public ReceivedFile(Context context, String fileName) {
        this.context = context;
        this.fileName = fileName;
    }


    public void store(String txt) {
        try (FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);) {
            fos.write(txt.getBytes(Utils.CHARSET));
        } catch (IOException e) {
            //
        }
    }

    public void store(short[] data) {
        try (FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);) {
            for (short d : data) {
                byte[] b = new byte[2];
                b[0] = (byte) (d & 0x00ff);
                b[1] = (byte) ((d >> 8) & 0x00ff);
                fos.write(b);
            }
        } catch (IOException e) {
            //
        }
    }

    public void storeStr(short[] data) {
        try (FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);) {
            for (short d : data) {
                fos.write(Short.toString(d).getBytes(StandardCharsets.UTF_8));
                fos.write('\n');
            }
        } catch (IOException e) {
            //
        }
    }

    public String read() {
        try (FileInputStream fos = context.openFileInput(fileName);) {
            return Utils.toString(fos);
        } catch (IOException e) {
            return "";
        }
    }


    public String head() {
        try (FileInputStream fos = context.openFileInput(fileName);) {
            final String s = Utils.toString(fos, 100);
            return s.trim();
        } catch (IOException e) {
            return "";
        }
    }


    public void handleIncomingDataIntent(@Nullable Intent data) {
        Log.i("Handling intent", data.toString());

        Uri uri = data.getData();
        ParcelFileDescriptor inputPFD;
        try {
            inputPFD = context.getContentResolver().openFileDescriptor(uri, "r");
        } catch (FileNotFoundException e) {
            Log.e("MainActivity", "File not found.");
            return;
        }

        FileDescriptor fd = inputPFD.getFileDescriptor();
        try (FileInputStream fis = new FileInputStream(fd);) {
            String txt = Utils.toString(fis);
            store(txt);
        } catch (IOException e) {
            Log.e("ReceivedFile", "Exception", e);
        }
    }


}
