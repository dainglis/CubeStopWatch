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
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class RecordsActivity extends AppCompatActivity
                             implements TimeDeleteDialogFragment.TimeDeleteDialogListener {

    ListView timesListView;
    int indexToDelete;
    List<Long> RAW_TIMES;
    List<String> TIMES;
    List<String> DATES;
    ArrayAdapter tAdapter;
    private SimpleAdapter sAdapter;

    ArrayList<HashMap<String, String>> timeList = new ArrayList<>();

    final String filename = "records";

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
            DATES.remove(indexToDelete);


            String data = "";
            for (int i = RAW_TIMES.size() -1; i >= 0; i--) {
                data = data + RAW_TIMES.get(i) + "/"
                    + DATES.get(i) + ";";
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
            DATES.clear();

            timeList.clear();

            readTimesFromFile();

            indexToDelete = -1;
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        String text = "Item not deleted";
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    /*
     * readTimesFromFile:
     *   reads from const filename and parses info into two lists
     *   info stored as x(*)/yyyy-MM-dd HH:mm:ss;
     *   takes x(*) into TIMES, yyyy-MM-dd HH:mm:ss into DATES, each at index i
     */
    public void readTimesFromFile() {
        FileInputStream inStream;
        String data = "";

        try {
            inStream = openFileInput(filename);
            int content;
            while ((content = inStream.read()) != -1) {
                if (content == '/') {
                    long rawTime = 0;
                    try {
                        rawTime = Integer.parseInt(data);
                    } catch (NumberFormatException nfe) {
                        nfe.printStackTrace();
                    }
                    RAW_TIMES.add(0, rawTime);
                    TIMES.add(0, Helper.convertTime(rawTime));
                    data = "";
                } else if (content == ';') {
                    DATES.add(0, data);
                    data = "";
                } else {
                    data += (char) content;
                }
            }
            inStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // called to update HashMap for ListView display
        writeHashMap();
    }

    /*
     * writeHashMap:
     *   builds timeItem HashMap from TIMES and DATES info, for two-line ListView display
     */
    public void writeHashMap() {
        HashMap<String, String> timeItem;
        for (int i = 0; i < TIMES.size(); i++) {
            timeItem = new HashMap<>();
            timeItem.put("line1", TIMES.get(i));
            // added two spaces for indentation padding
            timeItem.put("line2", "  " + DATES.get(i));
            timeList.add(timeItem);
        }
        // tAdapter no longer used, for single line list view
        // tAdapter.notifyDataSetChanged();
        sAdapter.notifyDataSetChanged();
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
        DATES = new ArrayList<>();
        indexToDelete = -1;

        //disabling this adapter for now
        //tAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, TIMES);

        //SimpleAdapter connects HashMap of times and dates with LinearLayout
        sAdapter = new SimpleAdapter(this,
                timeList,
                R.layout.content_records_twolinelist,
                new String[] {"line1", "line2"},
                new int[] {R.id.top_line_time, R.id.bottom_line_date});

        timesListView.setAdapter(sAdapter);
        readTimesFromFile();

        //onClick toast for debugging selected position
        timesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = DATES.get(position);
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });

        //onLongClick toast for debugging held position
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
