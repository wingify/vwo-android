package com.vwo.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.vwo.mobile.Vwo;

public class ExperimentText extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_text);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Object data = Vwo.getObjectForKey("bannerText", "Buy Now");

        if (data != null) {
            ((TextView) findViewById(R.id.buttonText)).setText(data.toString());

            Log.d("QOL", data.toString());
        }

        findViewById(R.id.buttonText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Vwo.markConversionForGoal("buttonClick");
            }
        });

        data = Vwo.getObjectForKey("bannerColor");

        if (data != null) {
            findViewById(R.id.banner).setBackgroundColor(Color.parseColor(data.toString()));
            Log.d("QOL", data.toString());
        } else {
            findViewById(R.id.banner).setBackgroundColor(Color.parseColor("#ffffff"));
        }

    }

}
