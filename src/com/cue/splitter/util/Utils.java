package com.cue.splitter.util;

import android.os.Environment;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 12.08.12
 * Time: 0:47
 * To change this template use File | Settings | File Templates.
 */
public class Utils {



    public static boolean isSdAvailable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return false;
        } else {
            return false;
        }
    }




}
