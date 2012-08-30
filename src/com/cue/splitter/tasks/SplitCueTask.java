package com.cue.splitter.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import com.cue.splitter.R;
import com.cue.splitter.data.CueFile;
import com.cue.splitter.util.CueSplitter;
import com.cue.splitter.util.Utils;
import com.cue.splitter.view.FileChooserDialog;
import com.cue.splitter.view.IFolderItemListener;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: GGobozov
 * Date: 23.08.12
 * Time: 14:13
 * To change this template use File | Settings | File Templates.
 */
public class SplitCueTask extends AsyncTask<Object, Integer, Boolean> implements IFolderItemListener {

    private Context context;
    private ProgressDialog splitDialog;
    private Handler progress;
    private CueFile cueFile;
    private Exception exception;
    private FileChooserDialog fileChooserDialog;
    private String path;

    public SplitCueTask(Context context, CueFile cueFile) {
        this.context = context;
        this.cueFile = cueFile;
        this.splitDialog = new ProgressDialog(context);
        this.splitDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        this.splitDialog.setCancelable(false);
        this.splitDialog.setMax(cueFile.getCheckedTracks().size());

        progress = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                publishProgress(msg.arg1);
            }
        };
    }

    @Override
    protected void onPreExecute() {
        this.splitDialog.setMessage(context.getString(R.string.splitting));
        this.splitDialog.show();

    }

    @Override
    protected Boolean doInBackground(Object... objects) {
        CueSplitter splitter = new CueSplitter(context);

        path = objects[0].toString();
        if (!path.endsWith("/"))
            path = path + "/";
        boolean result = false;
        try {
            result = splitter.splitCue(cueFile, path, progress);
        } catch (Exception e) {
            exception = e;
        }
        return result;
    }


    @Override
    protected void onPostExecute(Boolean o) {
        if (splitDialog.isShowing())
            splitDialog.dismiss();
        if (exception == null) {
            Utils.showMessageDialog(context, R.string.splitting_done);
        } else {
            if (exception instanceof FileNotFoundException)
                Utils.showMediaChooserDialog(context, R.string.cant_lookup_sound_file, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fileChooserDialog = new FileChooserDialog(context, false, "." + cueFile.getExtention());
                        fileChooserDialog.setTitle(R.string.select_media_file);
                        fileChooserDialog.setiFolderItemListener(SplitCueTask.this);
                        fileChooserDialog.show();
                    }
                });
            else if (exception instanceof TagException) ;
            else if (exception instanceof IOException)
                Utils.showMessageDialog(context, R.string.cant_write_sound_file);
            else if (exception instanceof Exception)
                Utils.showMessageDialog(context, R.string.cant_read_sound_file);
        }

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        splitDialog.setProgress(values[0]);
    }


    @Override
    public void OnCannotFileRead(File file) {

    }

    @Override
    public void OnFileClicked(File file) {
        if (fileChooserDialog.isShowing())
            fileChooserDialog.dismiss();
        cueFile.setFile(file.getName());
        new SplitCueTask(context, cueFile).execute(path);
    }

    @Override
    public void OnFolderChecked(File file) {

    }
}
