package com.vwo.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.vwo.mobile.VWO;
import com.vwo.mobile.VWOConfig;
import com.vwo.mobile.events.VWOStatusListener;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main Activity";
    //TODO: Add you VWO api key here
    private static final String VWO_APP_KEY = BuildConfig.VWO_API_KEY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Config for adding custom parameters before launch.
        Map<String, String> customKeys = new HashMap<>();
        customKeys.put("key", "value");
        VWOConfig vwoConfig = new VWOConfig
                .Builder()
                .setCustomSegmentationMapping(customKeys)
                .build();

        // Start VWO SDK in Async mode
        VWO.with(this, VWO_APP_KEY).config(vwoConfig).launch();
        // Config for adding custom parameters for after launch.
        VWO.setCustomVariable("key", "value");

        // Start VWO SDK in Async mode with callback
        VWO.with(this, VWO_APP_KEY).config(vwoConfig).launch(new VWOStatusListener() {
            @Override
            public void onVwoLoaded() {
                // VWO loaded successfully
            }

            @Override
            public void onVwoLoadFailure() {
                // VWO not loaded
            }
        });


        // Start VWO SDK in Sync mode
        VWO.with(this, VWO_APP_KEY).config(vwoConfig).launchSynchronously();

    }

    public void gotoNext(View v) {

        int id = v.getId();

        switch (id) {
            case R.id.exp_image:
                startActivity(new Intent(getApplicationContext(), ExperimentImages.class));
                VWO.markConversionForGoal("experimentImages");
                break;
            case R.id.exp_var:
                startActivity(new Intent(getApplicationContext(), ExperimentVariable.class));
                VWO.markConversionForGoal("variable", 9.0);
                break;
            case R.id.exp_text:
                startActivity(new Intent(getApplicationContext(), ExperimentText.class));
                VWO.markConversionForGoal("textAndButton");
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            VWO.markConversionForGoal("settings", 19.0);
            startActivity(new Intent(getApplicationContext(), DetailActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
