package com.dainglis.cubestopwatch;

/*
*   FILE    : PuzzleTimer
*   PROJECT : CSW
*   AUTHOR  : David Inglis
*   FIRST   : 2018-10-14
*   DESC    :
*       The PuzzleTimer object functions as a timer. The Timer can be stopped and started,
*       output of the Timer can be formatted into a printable string
*
*/

import android.os.SystemClock;

import java.util.Locale;

public class PuzzleTimer {
    private long timeStart, timeOffset, timeCounter;
    private boolean timing, paused;

    /*
    *   CLASS   : PuzzleTimer
    *   DESC    : Constructor
    *
    *   PARAMS  :
    *       void
    */
    public PuzzleTimer() {
        timeStart = 0;
        timeOffset = 0;
        timeCounter = 0;

        timing = true;
        paused = false;
    }

    /*
    *   METHOD  : start
    *   CLASS   : PuzzleTimer
    *   DESC    :
    *   PARAMS  :
    *       void
    *   RETURNS :
    *       bool :
    *           true if timer was successfully started
    *           false if timer is already started
    */
    public boolean start() {
        if (!timing) {
            timeStart = SystemClock.elapsedRealtime();
            timeOffset = 0;
            timing = true;
            paused = false;
            return true;
        } else {
            return false;
        }
    }

    /*
    *   METHOD  : pause
    *   CLASS   : PuzzleTimer
    *   DESC    :
    *   PARAMS  :
    *       void
    *   RETURNS :
    *       bool :
    *           true if timer was successfully paused
    *           false otherwise
    */
    public boolean pause() {
        if (!paused) {
            paused = true;
            timeCounter = SystemClock.elapsedRealtime();
            return true;
        } else {
            return false;
        }
    }

    /*
        *   METHOD  : pause
        *   CLASS   : PuzzleTimer
        *   DESC    :
        *   PARAMS  :
        *       void
        *   RETURNS :
        *       bool :
        *           true if timer was successfully paused
        *           false otherwise
        */
    public boolean resume() {
        if (timing && paused) {
            paused = false;
            timeOffset += SystemClock.elapsedRealtime() - timeCounter;
            return true;
        } else {
            return false;
        }
    }

    public boolean reset() {
        timing = false;
        paused = false;

        timeStart = 0;
        timeOffset = 0;
        timeCounter = 0;
        return true;
    }

    public boolean isTiming() {
        return timing;
    }

    public boolean isPaused() {
        return paused;
    }

    public long getTime() {
        return SystemClock.elapsedRealtime() - timeStart - timeOffset;
    }

    /*
    *   FUNCTION    : formatTime
    *   CLASS       : static PuzzleTimer
    *   DESC        :
    *       Given an unsigned long integer 'timeMS', formats the integer to a time
    *       String in the format "MM:SS.mmm" where:
    *       M - minute
    *       S - second
    *       m - millisecond
    *   PARAMS      :
    *       long timeMS : an unsigned long integer representing some time elapsed in
    *           milliseconds
    *   RETURNS     :
    *       String : formatted String determined by 'timeMS'
    */
    public static String formatTime(long timeMS) {
        int second = (int) (timeMS/1000);
        int minute = second/60;
        second %= 60;
        int millisecond = (int) (timeMS%1000);
        return ("" + String.format(Locale.getDefault(), "%02d", minute) + ":"
                + String.format(Locale.getDefault(), "%02d", second) + "."
                + String.format(Locale.getDefault(), "%03d", millisecond));
    }
}
