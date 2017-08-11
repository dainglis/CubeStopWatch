package com.dainglis.cubestopwatch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.util.Locale;

public class ClockMainActivity extends AppCompatActivity {

    ImageButton clockButton;
    Button buttonA, buttonB;
    TextView timerText;
    Handler timerHandler = new Handler();

    boolean timing, paused;

    long timeStart, timeMS, timeOffset, timeCounter;

    /*
     * String convertTime(long timeMS)
     * receives a time in milliseconds (long), returns String
     *   of the format "MMM:SS:XXX"
     */
    public static String convertTime(long timeMS) {
        //int rawTime = (int) timeMS;
        int second = (int) (timeMS/1000);
        int minute = second/60;
        second %= 60;
        int millisecond = (int) (timeMS%1000);
        //timerText.setText
        return ("" + String.format(Locale.getDefault(), "%02d", minute) + ":"
                + String.format(Locale.getDefault(), "%02d", second) + "."
                + String.format(Locale.getDefault(), "%03d", millisecond));
    }

    /*
     * void resetClock()
     * sets all aspects of the stopwatch back to default
     */
    public void resetClock() {
        toolbarDisable();
        timing = false;
        paused = false;
        timerText.setText(R.string.time_layout_blank);
    }

    /*
     * void toolbarDisable()
     * sets both buttons on the toolbar to disabled and invisible
     */
    public void toolbarDisable() {
        buttonA.setEnabled(false);
        buttonB.setEnabled(false);
        buttonA.setVisibility(View.INVISIBLE);
        buttonB.setVisibility(View.INVISIBLE);
    }

    /*
     * void toolbarEnable()
     * sets both buttons on the toolbar to enabled and visible
    */
    public void toolbarEnable() {
        buttonA.setEnabled(true);
        buttonB.setEnabled(true);
        buttonA.setVisibility(View.VISIBLE);
        buttonB.setVisibility(View.VISIBLE);
    }

    Runnable updateTimerThread = new Runnable() {
        @Override
        public void run() {
            timeMS = SystemClock.elapsedRealtime() - timeStart - timeOffset;
            String text = convertTime(timeMS);
            timerText.setText(text);
            timerHandler.postDelayed(this, 0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        timerText = (TextView) findViewById(R.id.timer_text);
        clockButton = (ImageButton) findViewById(R.id.inv_clock_button);
        buttonA = (Button) findViewById(R.id.button_A);
        buttonB = (Button) findViewById(R.id.button_B);

        resetClock();

        clockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!timing) {
                    // start timer
                    toolbarDisable();
                    timing = true;
                    paused = false;
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    timeStart = SystemClock.elapsedRealtime();
                    timeOffset = 0;
                    timerHandler.postDelayed(updateTimerThread, 0);
                } else if (!paused) {
                    // pause timer
                    toolbarEnable();
                    paused = true;
                    timeCounter = SystemClock.elapsedRealtime();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    timerHandler.removeCallbacks(updateTimerThread);
                } else {
                    // resume timer
                    toolbarDisable();
                    paused = false;
                    timeCounter = SystemClock.elapsedRealtime() - timeCounter;
                    timeOffset += timeCounter;
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    timerHandler.postDelayed(updateTimerThread, 0);
                }
            }
        });

        // button A is the SAVE button
        buttonA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String filename = "records";
                String data = String.format(Locale.getDefault(), "%d", timeMS) + ";";
                FileOutputStream outStream;

                try {
                    outStream = openFileOutput(filename, Context.MODE_APPEND);
                    outStream.write(data.getBytes());
                    outStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    Snackbar.make(view, "Time was not saved successfully", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }

                Snackbar.make(view, "Time saved in records", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                resetClock();
            }
        });

        // button B is the RESET button
        buttonB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Snackbar.make(view, "Button B was pressed", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                */
                resetClock();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_clock_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_records) {
            Intent intent = new Intent(this, RecordsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}