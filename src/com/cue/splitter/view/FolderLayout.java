package com.cue.splitter.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.cue.splitter.R;
import com.cue.splitter.data.CueFile;
import com.cue.splitter.data.Track;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;


public class FolderLayout extends LinearLayout implements AdapterView.OnItemClickListener {

    private Context context;
    private IFolderItemListener folderListener;
    private List<String> item = null;
    private List<String> path = null;
    private String root = "/";
    private TextView myPath;
    private ListView lstView;
    private boolean isFolderChooser;
    private Typeface font;

    public FolderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        font = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.folderview, this);

        myPath = (TextView) findViewById(R.id.path);
        lstView = (ListView) findViewById(R.id.list);

        getDir(root, lstView);

    }

    public void setIFolderItemListener(IFolderItemListener folderItemListener) {
        this.folderListener = folderItemListener;
    }

    //Set Directory for view at anytime
    public void setDir(String dirPath) {
        getDir(dirPath, lstView);
    }

    public void setFolderChooser(boolean folderChooser) {
        isFolderChooser = folderChooser;
    }

    private void getDir(String dirPath, ListView v) {
        myPath.setText("Path : " + dirPath);
        item = new ArrayList<String>();
        path = new ArrayList<String>();
        File f = new File(dirPath);
        File[] files = f.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                String fileName = file.getName();
                if (file.isDirectory() || fileName.endsWith(".cue") || fileName.endsWith(".CUE"))
                    return true;
                return false;
            }
        });

        if (!dirPath.equals(root)) {

            //item.add(root);
           // path.add(root);
            item.add("../");
            path.add(f.getParent());

        }
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            path.add(file.getPath());
            if (file.isDirectory())
                item.add(file.getName() + "/");
            else
                item.add(file.getName());

        }
        setItemList(item);

    }

    //can manually set Item to display, if u want
    public void setItemList(List<String> items) {
        FolderAdapter adapter = new FolderAdapter(context, R.layout.checkbox_row, items, isFolderChooser);
        lstView.setAdapter(adapter);
       // lstView.setOnItemClickListener(this);
    }


    public void onListItemClick(ListView l, View v, int position, long id) {
        File file = new File(path.get(position));
        if (file.isDirectory()) {
            if (file.canRead())
                getDir(path.get(position), l);
            else {
                //what to do when folder is unreadable
                if (folderListener != null) {
                    folderListener.OnCannotFileRead(file);

                }

            }
        } else {

            //what to do when file is clicked
            //You can add more,like checking extension,and performing separate actions
            if (folderListener != null) {
                folderListener.OnFileClicked(file);
            }

        }
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        onListItemClick((ListView) arg0, arg0, arg2, arg3);
    }

    public class FolderAdapter extends ArrayAdapter<String> {


        private Context context;
        private List<String> items;
        private boolean isFolderChooser;

        public FolderAdapter(Context context, int textViewResourceId, List<String> objects, boolean isFolderChooser) {
            super(context, textViewResourceId, objects);
            this.context = context;
            this.items = objects;
            this.isFolderChooser = isFolderChooser;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            final int pos = position;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.checkbox_row, null);
                holder = new ViewHolder();
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
                if (!isFolderChooser) {
                    holder.checkBox.setVisibility(View.GONE);
                }
                holder.text = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            holder.text.setTypeface(font);
            holder.text.setText(items.get(position));

            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    File file = new File(path.get(pos));
                    if (file.isDirectory()) {
                        if (file.canRead())
                            getDir(path.get(pos), lstView);
                        else {
                            //what to do when folder is unreadable
                            if (folderListener != null) {
                                folderListener.OnCannotFileRead(file);

                            }

                        }
                    } else {

                        //what to do when file is clicked
                        //You can add more,like checking extension,and performing separate actions
                        if (folderListener != null) {
                            folderListener.OnFileClicked(file);
                        }

                    }
                }
            });

            return convertView;
        }

        protected class ViewHolder {
            protected CheckBox checkBox;
            protected ImageView image;
            protected TextView text;

        }


    }


}
