package com.vwo.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.vwo.mobile.Vwo;
import com.vwo.mobile.events.VwoStatusListener;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main Activity";
    private static final String VWO_APP_KEY = "Your Vwo App Key";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Start VWO SDK in Sync mode
        Vwo.start(VWO_APP_KEY, getApplication());

        // Start VWO SDK in Async mode with callback
        Vwo.startAsync(VWO_APP_KEY, getApplication(), new VwoStatusListener() {
            @Override
            public void onVwoLoaded() {
                // VWO loaded successfully
            }

            @Override
            public void onVwoLoadFailure() {
                // VWO not loaded
            }
        });

        // Start VWO SDK in Async mode
        Vwo.startAsync(VWO_APP_KEY, getApplication());
    }

    public void gotoNext(View v) {

        int id = v.getId();

        switch (id) {
            case R.id.exp_image:
                startActivity(new Intent(getApplicationContext(), ExperimentImages.class));
                Vwo.markConversionForGoal("imageClicked");
                break;
            case R.id.exp_var:
                startActivity(new Intent(getApplicationContext(), ExperimentVariable.class));
                Vwo.markConversionForGoal("twoInone", 9.0);
                break;
            case R.id.exp_text:
                startActivity(new Intent(getApplicationContext(), ExperimentText.class));
                Vwo.markConversionForGoal("CCcode");
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
            Vwo.markConversionForGoal("CCcode", 19.0);
            startActivity(new Intent(getApplicationContext(), DetailActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
