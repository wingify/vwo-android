package com.vwo.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.vwo.mobile.Vwo;
import com.vwo.mobile.VwoConfig;

public class DetailActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Object data = Vwo.getObjectForKey("bannerText");

        if (data != null) {
            ((TextView) findViewById(R.id.text)).setText(data.toString());
            Log.d("QOL", data.toString());
        }


        data = Vwo.getObjectForKey("bannerColor");

        if (data != null) {
            findViewById(R.id.bg).setBackgroundColor(Color.parseColor(data.toString()));
            Log.d("QOL", data.toString());
        }

        Vwo.markConversionForGoal("goal123");
        Vwo.markConversionForGoal("goal456", 89.7);
        VwoConfig config = new VwoConfig.Builder().build();

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
