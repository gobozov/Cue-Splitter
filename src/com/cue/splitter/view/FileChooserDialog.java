package com.cue.splitter.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.cue.splitter.R;
import com.cue.splitter.util.Settings;
import com.cue.splitter.util.Utils;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: GGobozov
 * Date: 23.08.12
 * Time: 15:40
 * To change this template use File | Settings | File Templates.
 */
public class FileChooserDialog extends Dialog {

    private IFolderItemListener iFolderItemListener;

    public FileChooserDialog(Context context) {
        super(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filechooser);
        FolderLayout folderLayout = (FolderLayout)findViewById(R.id.localfolders);
        folderLayout.setFolderChooser(true);
        folderLayout.setIFolderItemListener(iFolderItemListener);
        folderLayout.setDir(Utils.isSdAvailable() ? Environment.getExternalStorageDirectory().getAbsolutePath() : "/");
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);


    }


    public void setiFolderItemListener(IFolderItemListener iFolderItemListener) {
        this.iFolderItemListener = iFolderItemListener;
    }

}
