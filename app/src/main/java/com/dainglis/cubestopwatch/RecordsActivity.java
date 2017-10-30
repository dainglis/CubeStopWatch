package com.dainglis.cubestopwatch;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecordsActivity extends AppCompatActivity
                             implements TimeDeleteDialogFragment.TimeDeleteDialogListener {

    ListView timesListView;
    int indexToDelete;
    List<Long> RAW_TIMES;
    List<String> TIMES;
    ArrayAdapter tAdapter;

    /*
     * void showTimeDeleteDialog()
     * pops up dialog asking user to confirm deletion of recorded time
     */
    public void showTimeDeleteDialog() {
        DialogFragment dialog = new TimeDeleteDialogFragment();
        dialog.show(getSupportFragmentManager(), "TimeDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if (indexToDelete >= 0) {
            //remove selected index from list, rewrite file with edited data, re-read
            RAW_TIMES.remove(indexToDelete);

            String filename = "records";
            String data = "";
            for (int i = RAW_TIMES.size() -1; i >= 0; i--) {
                data = data + RAW_TIMES.get(i) + ";";
            }
            FileOutputStream outStream;

            try {
                outStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outStream.write(data.getBytes());
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            RAW_TIMES.clear();
            TIMES.clear();
            readTimesFromFile();

            indexToDelete = -1;
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        String text = "Item not deleted";
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    public void readTimesFromFile() {
        FileInputStream inStream;
        String data = "";

        try {
            inStream = openFileInput("records");
            int content;
            while ((content = inStream.read()) != -1) {
                if (content == ';') {
                    long rawTime = 0;
                    try {
                        rawTime = Integer.parseInt(data);
                    } catch (NumberFormatException nfe) {
                        nfe.printStackTrace();
                    }
                    RAW_TIMES.add(0, rawTime);
                    TIMES.add(0, Helper.convertTime(rawTime));
                    data = "";
                } else {
                    data += (char) content;
                }
            }
            inStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        timesListView = (ListView) findViewById(R.id.times_list_view);

        RAW_TIMES = new ArrayList<>();
        TIMES = new ArrayList<>();
        indexToDelete = -1;

        tAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, TIMES);
        timesListView.setAdapter(tAdapter);
        readTimesFromFile();

        timesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String text = "Item " + String.format(Locale.getDefault(), "%d", position) + " long-pressed";
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                indexToDelete = position;
                showTimeDeleteDialog();
                return false;
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
