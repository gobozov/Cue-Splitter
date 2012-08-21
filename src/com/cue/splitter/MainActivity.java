package com.cue.splitter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.cue.splitter.data.CueFile;
import com.cue.splitter.data.Track;
import com.cue.splitter.exception.ReadSoundFileException;
import com.cue.splitter.soundfile.CheapSoundFile;
import com.cue.splitter.util.CueParser;
import com.cue.splitter.util.CueSplitter;
import com.cue.splitter.util.Settings;
import com.cue.splitter.util.Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends SherlockActivity {

    public static final int REQUEST_CUE_FILE = 5;
    public static final int REQUEST_FOLDER = 6;
    private static final String BUNDLE_IS_FOLDER_CHOOSER = "isFolderChooser";
    private static final String BUNDLE_CUE_FILE = "file";
    private static final String BUNDLE_FOLDER = "folder";


    private ListView trackList;
    private TextView text;
    private Typeface font;
    private TrackAdapter adapter;
    private CueFile cueFile;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        text = (TextView) findViewById(R.id.text);
        trackList = (ListView) findViewById(R.id.track_list);
        font = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");


    }

    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        menu.add(0, 1, 1, R.string.menu_select_cue).setIcon(R.drawable.ic_collection).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 2, 2, R.string.menu_cut_cue).setIcon(R.drawable.ic_csissors).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 3, 3, R.string.menu_cut_settings).setIcon(R.drawable.ic_action_settings).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        Intent intent = new Intent(MainActivity.this, FileChooserActivity.class);
        switch (item.getItemId()) {
            case 1:
                intent.putExtra(BUNDLE_IS_FOLDER_CHOOSER, false);
                startActivityForResult(intent, REQUEST_CUE_FILE);
                break;
            case 2:
                if (trackList.getCount() < 1) {
                    Toast.makeText(this, R.string.choose_cue_file, Toast.LENGTH_SHORT).show();
                    break;
                }
                if (Settings.getBoolean(this, Settings.PREF_DEFAULT_FOLDER_ENABLED)) {
                    if (cueFile != null) {
                        new ReadFileTask(this).execute(Settings.getString(this, Settings.PREF_DEFAULT_FOLDER_VALUE));
                    }
                } else {
                    intent.putExtra(BUNDLE_IS_FOLDER_CHOOSER, true);
                    startActivityForResult(intent, REQUEST_FOLDER);
                }
                break;
            case 3:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

        }
        return true;
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CUE_FILE) {
            File file = (File) data.getExtras().get(BUNDLE_CUE_FILE);
            processCueFile(file);
        }
        if (resultCode == RESULT_OK && requestCode == REQUEST_FOLDER) {
            String target = data.getExtras().getString(BUNDLE_FOLDER);
            if (cueFile != null && target != null) {
                new ReadFileTask(this).execute(target);
            }
        }
    }


    private class ReadFileTask extends AsyncTask<Object, Integer, CheapSoundFile> {

        private ProgressDialog readDialog;
        private Handler progress;
        private String path;

        public ReadFileTask(Context context) {
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
            this.readDialog.setMessage(getResources().getString(R.string.reading));
            this.readDialog.show();
        }


        @Override
        protected CheapSoundFile doInBackground(Object... objects) {
            CueSplitter splitter = new CueSplitter();
            path = objects[0].toString();
            if (!path.endsWith("/"))
                path = path + "/";
            try {
                CheapSoundFile cheapSoundFile = splitter.readTargetFile(cueFile, progress);
                return cheapSoundFile;
            } catch (IOException e) {
                Utils.showMessageDialog(MainActivity.this, R.string.smth_wrong, R.string.cant_read_sound_file);
            }
            return null;
        }


        @Override
        protected void onPostExecute(CheapSoundFile file) {
            if (readDialog.isShowing())
                readDialog.dismiss();
            new SplitCueTask(MainActivity.this).execute(file, path);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            readDialog.setProgress(values[0]);
        }
    }


    private class SplitCueTask extends AsyncTask<Object, Integer, Boolean> {

        private ProgressDialog splitDialog;
        private Handler progress;

        public SplitCueTask(Context context) {
            this.splitDialog = new ProgressDialog(context);
            this.splitDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.splitDialog.setCancelable(false);
            this.splitDialog.setMax(cueFile.getTracks().size());

            progress = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    publishProgress(msg.arg1);
                }
            };
        }

        @Override
        protected void onPreExecute() {
            this.splitDialog.setMessage(getResources().getString(R.string.splitting));
            this.splitDialog.show();

        }

        @Override
        protected Boolean doInBackground(Object... objects) {
            CueSplitter splitter = new CueSplitter();
            CheapSoundFile cheapSoundFile = (CheapSoundFile) objects[0];
            String path = objects[1].toString();
            if (!path.endsWith("/"))
                path = path + "/";
            boolean result = false;
            try {
                result = splitter.splitCue(cheapSoundFile, cueFile, path, progress);
            } catch (IOException e) {
                Utils.showMessageDialog(MainActivity.this, R.string.smth_wrong, R.string.cant_write_sound_file);
            }
            return result;
        }


        @Override
        protected void onPostExecute(Boolean o) {
            if (splitDialog.isShowing())
                splitDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
                splitDialog.setProgress(values[0]);
        }
    }

    private void processCueFile(File file) {
        CueParser parser = new CueParser();
        try {
            CueFile cue = parser.parse(file);
            if (cue.getTracks() != null && !cue.getTracks().isEmpty()) {
                adapter = new TrackAdapter(this, R.layout.checkbox_row, cue.getTracks(), cue);
                trackList.setAdapter(adapter);
                text.setVisibility(View.GONE);
                trackList.setVisibility(View.VISIBLE);
            }
            cueFile = cue;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class TrackAdapter extends ArrayAdapter<Track> {


        private Context context;
        private List<Track> tracks;
        private CueFile cueFile;

        public TrackAdapter(Context context, int textViewResourceId, List<Track> objects, CueFile cueFile) {
            super(context, textViewResourceId, objects);
            this.context = context;
            this.tracks = objects;
            this.cueFile = cueFile;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.checkbox_row, null);
                holder = new ViewHolder();
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
                holder.title = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Track t = tracks.get(position);
            holder.title.setTypeface(font);

            holder.title.setText(t.getTitle() + ((t.getPerformer() != null) ? " - " + t.getPerformer() : " - " + cueFile.getPerformer()));
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    t.setChecked(isChecked);
                }
            });
            holder.checkBox.setChecked(t.isChecked());
            return convertView;
        }

        protected class ViewHolder {
            protected CheckBox checkBox;
            protected TextView title;
        }


    }


}
