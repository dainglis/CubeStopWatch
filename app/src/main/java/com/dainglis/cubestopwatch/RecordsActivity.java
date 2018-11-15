package com.dainglis.cubestopwatch;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class RecordsActivity extends AppCompatActivity
                             implements TimeDeleteDialogFragment.TimeDeleteDialogListener {

    //TODO: refactor global variable usage of indexToDelete
    int indexToDelete;

    private SimpleAdapter sAdapter;

    ArrayList<HashMap<String, String>> timeList;
    ListView        timesListView;
    List<Long>      dataTimeRaw;
    List<String>    dataTime;
    List<String>    dataDate;

    //TODO:
    /* Read from file to get records file name (for 2x2 Cube, 3x3 Cube, Megaminx, Pyraminx, etc).
     * This is also needed in ClockMainActivity as it is currently dependent on a fixed "records" file
     *
     */
    final String file_userData = "user_data";

    //TODO: REMOVE DEPENDENCY using UserData class
    final String filename = "records";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        timesListView = findViewById(R.id.times_list_view);

        timeList = new ArrayList<>();
        dataTimeRaw = new ArrayList<>();
        dataTime = new ArrayList<>();
        dataDate = new ArrayList<>();

        indexToDelete = -1;

        //disabling this adapter for now
        //tAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataTime);

        //SimpleAdapter connects HashMap of times and dates with LinearLayout
        sAdapter = new SimpleAdapter(this,
                timeList,
                R.layout.content_records_twolinelist,
                new String[] {"line1", "line2"},
                new int[] {R.id.top_line_time, R.id.bottom_line_date});
        timesListView.setAdapter(sAdapter);

        readTimesFromFile();

        // called to update HashMap for ListView display
        writeHashMap();

        //onClick toast for debugging selected position
        timesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = dataDate.get(position);
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });

        //onLongClick toast for debugging held position
        timesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String text = "Item " + String.format(Locale.getDefault(), "%d", position) + " long-pressed";
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                // TODO: remove global variable usage
                indexToDelete = position;
                showTimeDeleteDialog(position);
                return true;
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    /*
     * void showTimeDeleteDialog()
     * pops up dialog asking user to confirm deletion of recorded time
     */
    public void showTimeDeleteDialog(int deleteIndex) {
        DialogFragment dialog = new TimeDeleteDialogFragment();
//        String temptext = String.format(Locale.getDefault(), "%d", dialog.getId()) + " is the dialog ID";
//        Toast.makeText(getApplicationContext(), temptext, Toast.LENGTH_SHORT).show();

        dialog.show(getSupportFragmentManager(), "TimeDialogFragment");
    }


    /*
     * void onDialogPositiveClick
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        //remove selected index from list, rewrite file with edited data, re-read

        if (indexToDelete >= 0) {

            // Remove selected index from Raw Time list and Date list
            dataTimeRaw.remove(indexToDelete);
            dataDate.remove(indexToDelete);

            // Write all time and date data to a data string
            String data = "";
            for (int i = dataTimeRaw.size() -1; i >= 0; i--) {
                data = data + dataTimeRaw.get(i) + "/"
                    + dataDate.get(i) + ";";
            }

            // Write data string to the active user data file
            // TODO: remove filename dependency by using the UserData class to get active file handle
            FileOutputStream outStream;
            try {
                outStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outStream.write(data.getBytes());
                outStream.close();
            }
            catch (FileNotFoundException e) {
                String exceptionText = "File not found for writing";
                Toast.makeText(getApplicationContext(), exceptionText, Toast.LENGTH_SHORT).show();

                System.out.println("EXCEPTION: Data file not found for writing");
                e.printStackTrace();
            }
            catch (Exception e) {
                System.out.println("EXCEPTION: Unhandled exception when writing times to data file");
                e.printStackTrace();
            }

            // Clear data array lists
            dataTimeRaw.clear();
            dataTime.clear();
            dataDate.clear();
            timeList.clear();

            // Re-read times from file for consistency
            readTimesFromFile();

            // called to update HashMap for ListView display
            writeHashMap();

            indexToDelete = -1;
        }
    }


    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        String text = "Item not deleted";
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        indexToDelete = -1;
    }


    /*
     * readTimesFromFile:
     *   reads from const filename and parses info into two lists
     *   info stored as x(*)/yyyy-MM-dd HH:mm:ss;
     *   takes x(*) into dataTime, yyyy-MM-dd HH:mm:ss into dataDate, each at index i
     *   example:
     *          19203/2018-01-01 10:45:31;
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
                    dataTimeRaw.add(0, rawTime);

                    dataTime.add(0, PuzzleTimer.formatTime(rawTime));

                    data = "";
                } else if (content == ';') {
                    dataDate.add(0, data);
                    data = "";
                } else {
                    data += (char) content;
                }
            }
            inStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * writeHashMap:
     *   builds timeItem HashMap from dataTime and dataDate info, for two-line ListView display
     */
    public void writeHashMap() {
        HashMap<String, String> timeItem;
        for (int i = 0; i < dataTime.size(); i++) {
            timeItem = new HashMap<>();
            timeItem.put("line1", dataTime.get(i));
            timeItem.put("line2", dataDate.get(i));
            timeList.add(timeItem);
        }
        sAdapter.notifyDataSetChanged();
    }
}