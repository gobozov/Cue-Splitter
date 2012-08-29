package com.cue.splitter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.cue.splitter.util.Utils;
import com.cue.splitter.view.FolderLayout;
import com.cue.splitter.view.IFolderItemListener;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 10.08.12
 * Time: 2:43
 * To change this template use File | Settings | File Templates.
 */
public class FileChooserActivity extends SherlockActivity implements IFolderItemListener {


    private FolderLayout localFolders;
    private boolean isFolderChooser;
    private String extension;
    private com.actionbarsherlock.view.ActionMode mMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filechooser);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        isFolderChooser = getIntent().getExtras().getBoolean("isFolderChooser");
        extension       = getIntent().getExtras().getString("extension");

        getSupportActionBar().setTitle(isFolderChooser ? R.string.select_folder : R.string.menu_select_cue);


        localFolders = (FolderLayout) findViewById(R.id.localfolders);
        localFolders.setFolderChooser(isFolderChooser);
        localFolders.setExtension(extension);
        localFolders.setIFolderItemListener(this);
        localFolders.setDir(Utils.isSdAvailable() ? Environment.getExternalStorageDirectory().getAbsolutePath() : "/");



    }


    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        if (isFolderChooser) {
            menu.add(0, 1, 1, R.string.menu_new_folder).setIcon(R.drawable.ic_content_new).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                final String parent = localFolders.getCurrentPath();
                Handler handler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        Boolean result = (Boolean) msg.obj;
                        if (!result)
                            Toast.makeText(FileChooserActivity.this, R.string.cant_create_folder, Toast.LENGTH_SHORT).show();
                        else
                            localFolders.setDir(parent);
                    }
                };
                CreateFolderDialog dialog = new CreateFolderDialog(FileChooserActivity.this, handler, parent);
                dialog.setTitle(R.string.create_new_folder);
                dialog.show();
                break;
        }
        return true;
    }

    //Your stuff here for Cannot open Folder
    @Override
    public void OnCannotFileRead(File file) {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.icon)
                .setTitle("[" + file.getName() + "] folder can't be read!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    //Your stuff here for file Click
    @Override
    public void OnFileClicked(final File file) {
        Intent intent = new Intent();
        intent.putExtra("file", file);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void OnFolderChecked(File file) {
        FileChooserActionMode actionMode = null;
        if (mMode == null){
             actionMode = new FileChooserActionMode(file);
             mMode = startActionMode(actionMode);
        }
        if (actionMode !=null){
             actionMode.setFile(file);
        }
    }

    private final class FileChooserActionMode implements com.actionbarsherlock.view.ActionMode.Callback {

        private File file;

        private FileChooserActionMode(File file) {
            this.file = file;
        }

        @Override
        public boolean onCreateActionMode(com.actionbarsherlock.view.ActionMode mode, com.actionbarsherlock.view.Menu menu) {
            //Used to put dark icons on light action bar
            menu.add(0, 1, 1, R.string.done).setIcon(R.drawable.ic_accept).setShowAsAction(com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_IF_ROOM);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(com.actionbarsherlock.view.ActionMode mode, com.actionbarsherlock.view.Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(com.actionbarsherlock.view.ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
            switch (item.getItemId()) {
                case 1:
                    Intent intent = new Intent();
                    intent.putExtra("folder", file.getAbsolutePath());
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
            }
            mMode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(com.actionbarsherlock.view.ActionMode mode) {
            mMode = null;
        }

        public void setFile(File file) {
            this.file = file;
        }
    }


}
