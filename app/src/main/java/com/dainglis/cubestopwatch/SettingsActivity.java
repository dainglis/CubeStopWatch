package com.dainglis.cubestopwatch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    ListView settingsListView;
    ArrayList<String> settingsItems = new ArrayList<>();
    private ArrayAdapter<String> sAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        settingsListView = (ListView) findViewById(R.id.settings_list_view);
        sAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                settingsItems);

        settingsListView.setAdapter(sAdapter);
        sAdapter.add("Testing number one");
        sAdapter.add("Testing number two");
        TextView licenseText = (TextView) findViewById(R.id.license_text_view);

        licenseText.setText(R.string.version_info);


    }

}
