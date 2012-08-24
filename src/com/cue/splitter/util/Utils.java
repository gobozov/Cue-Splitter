package com.cue.splitter.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

    public static void showMessageDialog(Context context, int dialogTitle){
        showMessageDialog(context, dialogTitle, -1);
    }


    public static void showMessageDialog(Context context, int dialogTitle, int message){
        final AlertDialog alert;
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(dialogTitle)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                if (message != -1)
                    builder.setMessage(message);
        alert = builder.create();
        alert.show();
    }




}
