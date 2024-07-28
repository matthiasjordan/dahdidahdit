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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.paddlesandbugs.dahdidahdit.R;
import com.paddlesandbugs.dahdidahdit.base.AbstractNavigationActivity;
import com.paddlesandbugs.dahdidahdit.base.MainActivity;

public class NetworkListActivity extends AbstractNavigationActivity {


    private ScrollView sv;

    private ListViewDBAdapter adapter;


    public static void callMe(Context context) {
        Intent intent = new Intent(context, NetworkListActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected String getHelpPageName() {
        return getString(R.string.helpurl_internet);
    }


    @Override
    protected int getLayoutID() {
        return R.layout.activity_network_list;
    }


    @Override
    protected int createTitleID() {
        return R.string.network_list_title;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity.setActivity(this, MainActivity.NETWORK_LIST);
    }


    @Override
    protected void onResume() {
        super.onResume();

        Log.i("NLA", "onResume()");

        adapter = new ListViewDBAdapter(this);

        ListView lv = findViewById(R.id.network_config_list);
        lv.setAdapter(adapter);

        final Context context = this;

        findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("NLA", "Add button clicked");
                NetworkAddActivity.callMe(context);
            }
        });

        ((ListView) findViewById(R.id.network_config_list)).setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("NLA", "Config clicked: " + position + "  id: " + id);
                NetworkConfig config = adapter.getItem(position);

                switch (config.protocol) {
                    case "mopp": {
                        MOPPClientActivity.callMe(context, config);
                        break;
                    }
                }
            }

        });

        ((ListView) findViewById(R.id.network_config_list)).setOnItemLongClickListener(new ListView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("NLA", "Config clicked long: " + position + "  id: " + id);
                NetworkConfig config = adapter.getItem(position);
                final String title = getResources().getString(R.string.network_list_delete_title);
                final String message = getResources().getString(R.string.network_list_delete_message, config.title);
                new AlertDialog.Builder(context) //
                        .setTitle(title) //
                        .setMessage(message) //
                        .setIcon(android.R.drawable.ic_dialog_alert) //
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                NetworkConfigDatabase.DBNetworkConfigProvider.create(context).delete(config.id);
                                adapter.notifyDataSetChanged();
                            }
                        }).setNegativeButton(android.R.string.no, null).show();

                return true;
            }

        });

    }


    @Override
    protected void onPause() {
        Log.i("NLA", "onPause()");
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private class ListViewDBAdapter extends BaseAdapter {


        private final List<NetworkConfig> configs = new ArrayList<>();

        private final Context context;


        public ListViewDBAdapter(Context context) {
            this.context = context;
            loadItems();
        }


        private void loadItems() {
            configs.clear();
            List<NetworkConfig> items = NetworkConfigDatabase.DBNetworkConfigProvider.create(context).getConfigs();
            configs.addAll(items);
        }


        @Override
        public void notifyDataSetChanged() {
            loadItems();
            super.notifyDataSetChanged();
        }


        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.activity_network_list_item, container, false);
            }

            final NetworkConfig config = getItem(position);
            ((TextView) convertView.findViewById(R.id.network_config_card_title)).setText(config.title);
            ((TextView) convertView.findViewById(R.id.network_config_card_address)).setText(config.address);
            return convertView;
        }


        @Override
        public int getCount() {
            return configs.size();
        }


        @Override
        public NetworkConfig getItem(int position) {
            return configs.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }


    }


}