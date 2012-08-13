package com.cue.splitter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
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
public class CreateFolderDialog extends Dialog{



    private Context context;
    private Handler handler;
    private TextView folderName;
    private Button okButton;
    private String parent;

    public CreateFolderDialog(Context context, Handler handler, String parent) {
        super(context);
        this.context = context;
        this.handler = handler;
        this.parent = parent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.folder_dialog);
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        folderName = (TextView) findViewById(R.id.folder_name);
        okButton = (Button) findViewById(R.id.ok);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = folderName.getText().toString().trim();
                if (name.length() > 0){
                     File newFolder = new File(parent, name);
                    Message message = new Message();
                    message.obj = new Boolean(newFolder.mkdir());
                    handler.sendMessage(message);
                } else {
                    Toast.makeText(context, R.string.input_folder_name, Toast.LENGTH_SHORT).show();
                }
                dismiss();
            }
        });


    }


}
