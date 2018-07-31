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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ClockMainActivity extends AppCompatActivity {

    ImageButton clockButton;
    Button buttonLeft, buttonRight;
    TextView timerText;
    Handler timerHandler = new Handler();

    boolean timing, paused;

    long timeStart, timeMS, timeOffset, timeCounter;

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
        buttonLeft.setEnabled(false);
        buttonRight.setEnabled(false);
        buttonLeft.setVisibility(View.INVISIBLE);
        buttonRight.setVisibility(View.INVISIBLE);
    }

    /*
     * void toolbarEnable()
     * sets both buttons on the toolbar to enabled and visible
    */
    public void toolbarEnable() {
        buttonLeft.setEnabled(true);
        buttonRight.setEnabled(true);
        buttonLeft.setVisibility(View.VISIBLE);
        buttonRight.setVisibility(View.VISIBLE);
    }

    Runnable updateTimerThread = new Runnable() {
        @Override
        public void run() {
            timeMS = SystemClock.elapsedRealtime() - timeStart - timeOffset;
            String text = Conversion.convertTime(timeMS);
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
        buttonLeft = (Button) findViewById(R.id.button_left_save);
        buttonRight = (Button) findViewById(R.id.button_right_reset);

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

        // buttonLeft is the SAVE button
        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: REMOVE DEPENDENCY
                String filename = "records";
                String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Calendar.getInstance().getTime());

                String data = String.format(Locale.getDefault(), "%d", timeMS) + "/" + formattedDate + ";";
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

                Snackbar.make(view, "Time " + data + " saved in records", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                resetClock();
            }
        });

        // buttonRight is the RESET button
        buttonRight.setOnClickListener(new View.OnClickListener() {
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
