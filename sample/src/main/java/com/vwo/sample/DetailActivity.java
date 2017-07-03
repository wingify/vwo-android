package com.vwo.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.vwo.mobile.VWO;
import com.vwo.mobile.VWOConfig;

public class DetailActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Object data = VWO.getObjectForKey("bannerText");

        if (data != null) {
            ((TextView) findViewById(R.id.text)).setText(data.toString());
            Log.d("QOL", data.toString());
        }


        data = VWO.getObjectForKey("bannerColor");

        if (data != null) {
            findViewById(R.id.bg).setBackgroundColor(Color.parseColor(data.toString()));
            Log.d("QOL", data.toString());
        }

        VWO.markConversionForGoal("goal123");
        VWO.markConversionForGoal("goal456", 89.7);
        VWOConfig config = new VWOConfig.Builder().build();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
