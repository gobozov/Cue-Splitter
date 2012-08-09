package com.cue.splitter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockActivity;

public class MainActivity extends SherlockActivity {

    private Button buttonSelectCue;
    private ListView trackList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        buttonSelectCue = (Button) findViewById(R.id.button_select);
        buttonSelectCue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, FileChooserActivity.class));
            }
        });


    }
}
