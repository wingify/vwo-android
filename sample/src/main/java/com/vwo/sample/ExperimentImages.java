package com.vwo.sample;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.vwo.mobile.VWO;

public class ExperimentImages extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_images);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                VWO.markConversionForGoal("backgroundLoaded");
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView varImage = (ImageView) findViewById(R.id.var_image);

        Object urlObject = VWO.getVariationForKey("bannerURL");

        if (urlObject != null) {
            Toast.makeText(this, urlObject.toString(), Toast.LENGTH_SHORT).show();
            Picasso.with(this).load(urlObject.toString()).into(varImage);
        } else {
            String url = "http://img15.deviantart.net/92c4/i/2010/039/6/3/__green_gradient_background___by_iristhefaerie.jpg";
            Picasso.with(this).load(url).into(varImage);
        }
    }

}
