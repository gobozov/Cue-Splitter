package com.cue.splitter.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.cue.splitter.R;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class FolderLayout extends LinearLayout {

    private Context context;
    private IFolderItemListener folderListener;
    private List<String> items = null;
    private List<PathWrapper> paths = null;
    private String root = "/";
    private TextView myPath;
    private ListView lstView;
    private boolean isFolderChooser;
    private Typeface font;
    private String currentPath;

    public FolderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        font = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.folderview, this);

        myPath = (TextView) view.findViewById(R.id.path);
        lstView = (ListView) view.findViewById(R.id.list);

        getDir(root, lstView);

    }

    public void setIFolderItemListener(IFolderItemListener folderItemListener) {
        this.folderListener = folderItemListener;
    }

    //Set Directory for view at anytime
    public void setDir(String dirPath) {
        getDir(dirPath, lstView);
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public void setFolderChooser(boolean folderChooser) {
        isFolderChooser = folderChooser;
    }

    FileFilter cueFolderFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            String fileName = file.getName();
            if (file.isDirectory() || fileName.endsWith(".cue") || fileName.endsWith(".CUE"))
                return true;
            return false;
        }
    };

    FileFilter folderFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            return file.isDirectory();
        }
    };

    private void getDir(String dirPath, ListView v) {
        currentPath = dirPath;
        myPath.setText("Path : " + dirPath);
        items = new ArrayList<String>();
        paths = new ArrayList<PathWrapper>();
        File f = new File(dirPath);
        File[] files = f.listFiles(isFolderChooser ? folderFilter : cueFolderFilter);
        if (!f.getPath().equals(root)) {

            items.add("../");
            paths.add(new PathWrapper(f.getParent()));

        }
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            paths.add(new PathWrapper(file.getPath()));
            if (file.isDirectory())
                items.add(file.getName() + "/");
            else
                items.add(file.getName());

        }
        setItemList(items);

    }

    //can manually set Item to display, if u want
    public void setItemList(List<String> items) {
        FolderAdapter adapter = new FolderAdapter(context, R.layout.radiobutton_row, items, isFolderChooser);
        lstView.setAdapter(adapter);
    }


    public static class PathWrapper {

        String path;
        boolean isChecked;


        public PathWrapper(String path) {
            this.path = path;
        }
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
            final String text = items.get(position);
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.radiobutton_row, null);
                holder = new ViewHolder();
                holder.radioButton = (RadioButton) convertView.findViewById(R.id.radiobutton);
                holder.image = (ImageView) convertView.findViewById(R.id.image);
                holder.text = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            holder.text.setTypeface(font);
            holder.text.setText(text);
            holder.radioButton.setVisibility((!isFolderChooser || text.equals("../")) ? View.GONE : View.VISIBLE);

            if (text.endsWith(".cue") || text.endsWith(".CUE"))
                holder.image.setBackgroundResource(R.drawable.ic_cue);
            else
                holder.image.setBackgroundResource(R.drawable.ic_folder);


            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    File file = new File(paths.get(pos).path);
                    if (file.isDirectory()) {
                        if (file.canRead())
                            getDir(paths.get(pos).path, lstView);
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

            holder.radioButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (PathWrapper p : paths)
                        p.isChecked = false;
                    notifyDataSetChanged();
                    File folder = new File(paths.get(pos).path);
                    if (folder.canRead()) {
                        paths.get(pos).isChecked = true;
                        folderListener.OnFolderChecked(folder);
                        notifyDataSetChanged();
                    } else {
                        //what to do when folder is unreadable
                        if (folderListener != null) {
                            folderListener.OnCannotFileRead(folder);
                        }

                    }
                }
            });

            holder.radioButton.setChecked(paths.get(position).isChecked);
            return convertView;
        }

        protected class ViewHolder {
            protected RadioButton radioButton;
            protected ImageView image;
            protected TextView text;

        }


    }


}
