package com.cue.splitter.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import com.cue.splitter.R;
import com.cue.splitter.data.CueFile;
import com.cue.splitter.soundfile.CheapSoundFile;
import com.cue.splitter.util.CueSplitter;
import com.cue.splitter.util.Utils;
import com.cue.splitter.view.FileChooserDialog;
import com.cue.splitter.view.IFolderItemListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: GGobozov
 * Date: 23.08.12
 * Time: 14:08
 * To change this template use File | Settings | File Templates.
 */
public class ReadFileTask extends AsyncTask<Object, Integer, CheapSoundFile> implements IFolderItemListener {

    private Context context;
    private CueFile cueFile;
    private ProgressDialog readDialog;
    private FileChooserDialog fileChooserDialog;
    private Handler progress;
    private String path;
    private Exception exception;

    public ReadFileTask(Context context, CueFile cueFile) {
        this.context = context;
        this.cueFile = cueFile;
        this.readDialog = new ProgressDialog(context);
        this.readDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        this.readDialog.setCancelable(false);

        progress = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                publishProgress(msg.arg1);
            }
        };
    }

    @Override
    protected void onPreExecute() {
        this.readDialog.setMessage(context.getString(R.string.reading));
        this.readDialog.show();
    }


    @Override
    protected CheapSoundFile doInBackground(Object... objects) {
        CueSplitter splitter = new CueSplitter(context);
        path = objects[0].toString();
        if (!path.endsWith("/"))
            path = path + "/";
        try {
            CheapSoundFile cheapSoundFile = splitter.readTargetFile(cueFile, progress);
            return cheapSoundFile;
        } catch (IOException e) {
            e.printStackTrace();
            exception = e;

        }
        return null;
    }


    @Override
    protected void onPostExecute(CheapSoundFile file) {
        if (readDialog.isShowing())
            readDialog.dismiss();
        if (exception == null) {
            new SplitCueTask(context, cueFile).execute(file, path);
        } else {
            if (exception instanceof FileNotFoundException)
                Utils.showMediaChooserDialog(context, R.string.cant_lookup_sound_file, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fileChooserDialog = new FileChooserDialog(context, false, "." + cueFile.getExtention());
                        fileChooserDialog.setTitle(R.string.select_media_file);
                        fileChooserDialog.setiFolderItemListener(ReadFileTask.this);
                        fileChooserDialog.show();
                    }
                });
            else
                Utils.showMessageDialog(context, R.string.cant_read_sound_file);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        readDialog.setProgress(values[0]);
    }

    @Override
    public void OnCannotFileRead(File file) {

    }

    @Override
    public void OnFileClicked(File file) {
        if (fileChooserDialog.isShowing())
            fileChooserDialog.dismiss();
        cueFile.setFile(file.getName());
        new ReadFileTask(context, cueFile).execute(path);
    }

    @Override
    public void OnFolderChecked(File file) {

    }
}
