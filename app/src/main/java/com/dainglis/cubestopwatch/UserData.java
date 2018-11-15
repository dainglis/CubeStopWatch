/*
*   PROJECT : CubeStopWatch
*   FILE    : UserData.java
*   FIRST   : 2018-07-30
*   DESC    :
*       Class deals with reading and writing user data.
*/


package com.dainglis.cubestopwatch;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
//import java.util.ArrayList;

/*
 * Created by david on 7/30/2018.
 */
class UserData {
//    public ArrayList<String> datafiles = new ArrayList<>();
//    public String datafiles_identifiers = "datafiles_identifiers";

    private static final String userdata_filename = "userdata";
    static Context appContext;

    static void initializeUserData() {
        FileOutputStream outStream;
        FileInputStream inStream;

        try {
            inStream = appContext.openFileInput(userdata_filename);
            System.out.println("USERDATA: File opened successfully");

            inStream.close();
        } catch (FileNotFoundException inException) {
            System.out.println("USERDATA: The file does not exist");
            try {
                outStream = appContext.openFileOutput(userdata_filename, Context.MODE_PRIVATE);
                String data = "Times";
                outStream.write(data.getBytes());
                outStream.close();
                System.out.println("USERDATA: The userdata file was created");
            } catch (FileNotFoundException outException) {
                System.out.println("USERDATA: The file cannot be created");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

        } catch (Exception e) {
            System.out.println("An abstract exception has been thrown");
            e.printStackTrace();
        }
    }

    static void addFileHandle(String title) {

    }

    static void getFileHandle() {

    }
}
