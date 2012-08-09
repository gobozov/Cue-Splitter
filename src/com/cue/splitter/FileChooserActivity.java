package com.cue.splitter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
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
public class FileChooserActivity extends SherlockActivity implements IFolderItemListener {

    FolderLayout localFolders;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filechooser);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        localFolders = (FolderLayout) findViewById(R.id.localfolders);
        localFolders.setIFolderItemListener(this);
        localFolders.setDir(Environment.getExternalStorageDirectory().getAbsolutePath());



    }

    //Your stuff here for Cannot open Folder
    @Override
    public void OnCannotFileRead (File file){
        // TODO Auto-generated method stub
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.icon)
                .setTitle(
                        "[" + file.getName()
                                + "] folder can't be read!")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {


                            }
                        }).show();

    }


    //Your stuff here for file Click
    @Override
    public void OnFileClicked (File file){
        // TODO Auto-generated method stub
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.icon)
                .setTitle("[" + file.getName() + "]")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {


                            }

                        }).show();
    }



}
