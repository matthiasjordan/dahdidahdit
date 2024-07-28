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

package com.paddlesandbugs.dahdidahdit.network;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.Utils;
import com.paddlesandbugs.dahdidahdit.base.AbstractNavigationActivity;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;

public class NetworkAddActivity extends AbstractNavigationActivity {


    public static void callMe(Context context) {
        Intent intent = new Intent(context, NetworkAddActivity.class);
        context.startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    @Override
    protected int getLayoutID() {
        return R.layout.activity_network_add;
    }


    @Override
    protected int createTitleID() {
        return R.string.network_add_title;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity.setActivity(this, MainActivity.NETWORK_LIST);
    }


    @Override
    protected void onResume() {
        super.onResume();

        Log.i("NAA", "onResume()");

        final Context context = this;

        findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = ((TextInputEditText) findViewById(R.id.network_add_field_description)).getText().toString();
                String address = ((TextInputEditText) findViewById(R.id.network_add_field_address)).getText().toString();
                if (!Utils.isEmpty(title) && !Utils.isEmpty(address)) {
                    NetworkConfig config = new NetworkConfig(null, title, "mopp", address, "");
                    NetworkConfigDatabase.DBNetworkConfigProvider.create(context).store(config);
                }
                ((NetworkAddActivity) context).finish();
            }

        });

    }


}