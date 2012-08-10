package com.cue.splitter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import com.actionbarsherlock.app.SherlockActivity;
import com.cue.splitter.view.FolderLayout;
import com.cue.splitter.view.IFolderItemListener;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 10.08.12
 * Time: 2:43
 * To change this template use File | Settings | File Templates.
 */
public class FileChooserDialog extends Dialog implements IFolderItemListener {



    private Context context;
    private Handler handler;
    private FolderLayout localFolders;
    private boolean isFolderChooser;

    public FileChooserDialog(Context context, Handler handler, boolean isFolderChooser) {
        super(context);
        this.context = context;
        this.handler = handler;
        this.isFolderChooser = isFolderChooser;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filechooser);

        localFolders = (FolderLayout) findViewById(R.id.localfolders);
        localFolders.setFolderChooser(isFolderChooser);
        localFolders.setIFolderItemListener(this);
        localFolders.setDir(Environment.getExternalStorageDirectory().getAbsolutePath());

    }

    //Your stuff here for Cannot open Folder
    @Override
    public void OnCannotFileRead (File file){
        // TODO Auto-generated method stub
        new AlertDialog.Builder(context)
                .setIcon(R.drawable.icon)
                .setTitle("[" + file.getName() + "] folder can't be read!")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
    }

    //Your stuff here for file Click
    @Override
    public void OnFileClicked (final File file){
        Message message = new Message();
        message.obj = file;
        handler.sendMessage(message);
        FileChooserDialog.this.dismiss();
    }



}
