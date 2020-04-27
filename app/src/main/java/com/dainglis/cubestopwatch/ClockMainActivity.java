/*
*   PROJECT : CubeStopWatch (working title)
*   AUTHOR  : David Inglis
*   FIRST   : 2017-08-11
*   DESC    :
*       A timer for Rubik's cubes and other puzzles.
*
*   TODO:
*   *   Lower screen brightness after timer runs for ?10 minutes, keep full functionality
*/


package com.dainglis.cubestopwatch;

import android.annotation.SuppressLint; //TODO: write custom ImageButton View to allow for View#performClick() override
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
    ImageButton timerButton;
    Button buttonLeft, buttonRight;
    TextView timerText, readyText;
    Toolbar toolbar;

    // Timer elements
    boolean midPressTimer = false;
    PuzzleTimer timer = new PuzzleTimer();
    Handler timerHandler = new Handler();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_main);

        // Set up toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find element views
        readyText = findViewById(R.id.ready_text);
        timerText = findViewById(R.id.timer_text);
        timerButton = findViewById(R.id.inv_clock_button);
        buttonLeft = findViewById(R.id.button_left_save);
        buttonRight = findViewById(R.id.button_right_reset);


        // Checks that the user data file exists, and creates it if it does not
//        initializeUserData();
        UserData.appContext = getApplicationContext();
        UserData.initializeUserData();

        timer.reset();
        activityStateReset();

        /*
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
                    System.out.println("DEBUG: Pause timer - this should not be reached");
                    activityStateTimerInactive();
                    timer.pause();

                } else {
                    // DEPRECATED
                    // TODO: refactor this resume action to match deprecations
                    System.out.println("DEBUG: Resume timer - this should not be reached");
                    activityStateTimerActive();
                    timer.resume();

                }
            }
        });
        */


        timerButton.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!timer.isTiming()) {
                            // Ready the timer state
                            System.out.println("DEBUG: Ready timer...");
                            activityStateTimerReady();
                            return true;
                        }
                        else if (!timer.isPaused()) {
                            // Pause the timer as soon as user touches screen
                            System.out.println("DEBUG: Pause timer");
                            timer.pause();
                            activityStateTimerInactive();
                            updateTimerPost();
                            midPressTimer = true;
                            return true;
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        if (!timer.isTiming()) {
                            // Begin the timer when user releases the screen
                            System.out.println("DEBUG: Start timer"); //DEBUG
                            timer.start();
                            activityStateTimerActive();
                            return true;
                        }
                        else if (midPressTimer) {
                            // Ensures that the timer is not resumed right after it is paused
                            midPressTimer = false;
                        }
                        else if (timer.isPaused()) {
                            // Resume the timer when user releases the screen
                            System.out.println("DEBUG: Resume timer");
                            timer.resume();
                            activityStateTimerActive();
                            return true;
                        }
                        break;
                }

                return false;
            }
        });

        // buttonLeft is the SAVE button
        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO:
                // pass information to writing function saveToActiveFile
                //  - get active file name
                //  - open file handle
                //  - write formatted time and date to file
                //  - close file handle

                //saveToActiveFile();

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
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Snackbar.make(view, "Time was not saved successfully", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }


                Snackbar.make(view, "Time " + data + " saved in records", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();

                timer.reset(); // should move to activityStateReset
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
                timer.reset(); // should move to activityStateReset
                activityStateReset();
            }
        });
    }

    public void saveToActiveFile() {
        // pass information to writing function saveToActiveFile
        //  - get active file name
        //  - open file handle
        //  - write formatted time and date to file
        //  - close file handle

    }

    Runnable updateTimerThread = new Runnable() {
        @Override
        public void run() {
            // This runnable is used to update the screen with current timer time
            updateTimerPost();
            timerHandler.postDelayed(this, 0);
        }
    };

    void updateTimerPost() {
        long gTime = timer.getTime();
        String text = PuzzleTimer.formatTime(gTime);

        timerText.setText(text);
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
        }
        else if (id == R.id.action_records) {
            Intent intent = new Intent(this, RecordsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /*
     * void toolbarDisable()
     * sets both buttons on the toolbar to disabled and invisible
     */
    public void buttonsDisabled() {
        buttonLeft.setEnabled(false);
        buttonLeft.setVisibility(View.INVISIBLE);

        buttonRight.setEnabled(false);
        buttonRight.setVisibility(View.INVISIBLE);
    }

    /*
     * void toolbarEnable()
     * sets both buttons on the toolbar to enabled and visible
    */
    public void buttonsEnabled() {
        buttonLeft.setEnabled(true);
        buttonLeft.setVisibility(View.VISIBLE);

        buttonRight.setEnabled(true);
        buttonRight.setVisibility(View.VISIBLE);
    }

    /*
     * void resetClock()
     * sets all aspects of the stopwatch back to default
     */
    public void activityStateReset() {
        System.out.println("DEBUG: Activity reset");
        buttonsDisabled();

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
