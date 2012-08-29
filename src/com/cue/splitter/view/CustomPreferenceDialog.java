package com.cue.splitter.view;

import android.content.Context;
import android.os.Environment;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.cue.splitter.R;
import com.cue.splitter.util.Settings;
import com.cue.splitter.util.Utils;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: GGobozov
 * Date: 13.08.12
 * Time: 17:00
 * To change this template use File | Settings | File Templates.
 */
public class CustomPreferenceDialog extends DialogPreference implements IFolderItemListener{

    private Context context;
    private File file;

    public CustomPreferenceDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

    }

    @Override
    protected View onCreateDialogView() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.filechooser, null);
        FolderLayout folderLayout = (FolderLayout)view.findViewById(R.id.localfolders);
        folderLayout.setFolderChooser(true);
        folderLayout.setIFolderItemListener(this);
        folderLayout.setDir(Utils.isSdAvailable() ? Environment.getExternalStorageDirectory().getAbsolutePath() : "/");
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        return view;

    }



    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult && file != null){
            Settings.setString(context, Settings.PREF_DEFAULT_FOLDER_VALUE, file.getAbsolutePath());
        }

    }

    @Override
    public void OnCannotFileRead(File file) {
        Toast.makeText(context, R.string.folder_cannot_read, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void OnFileClicked(File file) {
    }

    @Override
    public void OnFolderChecked(File file) {
        this.file = file;
    }
}
