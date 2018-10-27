/*
*   PROJECT : CubeStopWatch (working title)
*   AUTHOR  : David Inglis
*   FIRST   : 2017-08-11
*   DESC    :
*/


package com.dainglis.cubestopwatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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

    // Layout elements
    PuzzleTimer timer = new PuzzleTimer();
    Handler timerHandler = new Handler();

<<<<<<< HEAD
<<<<<<< HEAD
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
=======
    ImageButton timerButton;
    Button buttonLeft, buttonRight;
    TextView timerText, readyText;
>>>>>>> Created PuzzleTimer object to handle all timer actions. Code is being refactored
=======
    ImageButton timerButton;
    Button buttonLeft, buttonRight;
    TextView timerText, readyText;
>>>>>>> master

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        timerText = (TextView) findViewById(R.id.timer_text);
        readyText = (TextView) findViewById(R.id.ready_text);
        timerButton = (ImageButton) findViewById(R.id.inv_clock_button);
        buttonLeft = (Button) findViewById(R.id.button_left_save);
        buttonRight = (Button) findViewById(R.id.button_right_reset);

        activityStateReset();

        // timerButton
        timerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!timer.isTiming()) {
                    // DEPRECATED, timer starts on button UP touch listener
                    System.out.println("DEBUG: Start timer - this should not be reached");
                    activityStateTimerActive();
                    timer.start();

                } else if (!timer.isPaused()) {
                    // DEPRECATED
                    // TODO: pause on button down
                    System.out.println("DEBUG: Pause timer");
                    activityStateTimerInactive();
                    timer.pause();

                } else {
                    // DEPRECATED
                    // TODO: refactor this resume action to match deprecations
                    System.out.println("DEBUG: Resume timer");
                    activityStateTimerActive();
                    timer.resume();

                }
            }
        });

        timerButton.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (!timer.isTiming()) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        activityStateTimerReady();
                        return true;
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        System.out.println("DEBUG: Start timer");
                        activityStateTimerActive();
                        timer.start();
                        return true;
                    }
                }
                return false;
            }
        });

        // buttonLeft is the SAVE button
        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
<<<<<<< HEAD
<<<<<<< HEAD
                //TODO: REMOVE DEPENDENCY
=======
                //TODO: remove hardcoded filename, need flexible filenames'
>>>>>>> Created PuzzleTimer object to handle all timer actions. Code is being refactored
=======
                //TODO: remove hardcoded filename, need flexible filenames'
>>>>>>> master
                String filename = "records";
                String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Calendar.getInstance().getTime());


                String data = String.format(Locale.getDefault(), "%d", timer.getTime()) + "/" + formattedDate + ";";
                //String data = String.format(Locale.getDefault(), "%d", timeMS) + "/" + formattedDate + ";";
                FileOutputStream outStream;

                try {
                    outStream = openFileOutput(filename, Context.MODE_APPEND);
                    outStream.write(data.getBytes());
                    outStream.close();
                    System.out.println("DEBUG: " + data + " saved successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                    Snackbar.make(view, "Time was not saved successfully", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }


                Snackbar.make(view, "Time " + data + " saved in records", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                activityStateReset();
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
                activityStateReset();
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

    Runnable updateTimerThread = new Runnable() {
        @Override
        public void run() {
            // this runnable is used to update the screen with current timer time
            long gTime = timer.getTime();

            String text = PuzzleTimer.formatTime(gTime);

            timerText.setText(text);

            timerHandler.postDelayed(this, 0);
        }
    };


    /*
     * void toolbarDisable()
     * sets both buttons on the toolbar to disabled and invisible
     */
    public void buttonsDisabled() {
        buttonLeft.setEnabled(false);
        buttonRight.setEnabled(false);
        buttonLeft.setVisibility(View.INVISIBLE);
        buttonRight.setVisibility(View.INVISIBLE);
    }

    /*
     * void toolbarEnable()
     * sets both buttons on the toolbar to enabled and visible
    */
    public void buttonsEnabled() {
        buttonLeft.setEnabled(true);
        buttonRight.setEnabled(true);
        buttonLeft.setVisibility(View.VISIBLE);
        buttonRight.setVisibility(View.VISIBLE);
    }

    /*
     * void resetClock()
     * sets all aspects of the stopwatch back to default
     */
    public void activityStateReset() {
        System.out.println("DEBUG: Activity reset");
        buttonsDisabled();
        timer.reset();

        readyText.setVisibility(View.INVISIBLE);
        timerText.setText(R.string.time_layout_blank);
    }

    public void activityStateTimerActive() {
        // activates timerHandler Runnable for screen updating
        timerHandler.postDelayed(updateTimerThread, 0);
        readyText.setVisibility(View.INVISIBLE);

        buttonsDisabled();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void activityStateTimerInactive() {
        // deactivates timerHandler Runnable
        timerHandler.removeCallbacks(updateTimerThread);

        buttonsEnabled();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void activityStateTimerReady() {
        System.out.println("DEBUG: Timer ready to begin");
        //readyText.setText(R.string.timer_ready_text);
        readyText.setVisibility(View.VISIBLE);
    }

}
