package com.dainglis.cubestopwatch;

import java.util.Locale;

/**
 * Created by David on 3/10/2017.
 */

public class Conversion {
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
}
