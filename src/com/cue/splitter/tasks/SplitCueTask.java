package com.cue.splitter.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import com.cue.splitter.R;
import com.cue.splitter.data.CueFile;
import com.cue.splitter.soundfile.CheapSoundFile;
import com.cue.splitter.util.CueSplitter;
import com.cue.splitter.util.Utils;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: GGobozov
 * Date: 23.08.12
 * Time: 14:13
 * To change this template use File | Settings | File Templates.
 */
public class SplitCueTask extends AsyncTask<Object, Integer, Boolean> {

    private Context context;
    private ProgressDialog splitDialog;
    private Handler progress;
    private CueFile cueFile;
    private Exception exception;

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
        CheapSoundFile cheapSoundFile = (CheapSoundFile) objects[0];
        String path = objects[1].toString();
        if (!path.endsWith("/"))
            path = path + "/";
        boolean result = false;
        try {
            result = splitter.splitCue(cheapSoundFile, cueFile, path, progress);
        } catch (IOException e) {
            exception = e;
        }
        return result;
    }


    @Override
    protected void onPostExecute(Boolean o) {
        if (splitDialog.isShowing())
            splitDialog.dismiss();
          Utils.showMessageDialog(context, R.string.splitting_done);
        if (exception != null)
            Utils.showMessageDialog(context, R.string.smth_wrong, R.string.cant_write_sound_file);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        splitDialog.setProgress(values[0]);
    }
}
