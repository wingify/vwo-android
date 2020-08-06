package com.vwo.sampleapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.facebook.stetho.Stetho;
import com.vwo.mobile.VWO;
import com.vwo.mobile.VWOConfig;
import com.vwo.mobile.events.VWOStatusListener;
import com.vwo.mobile.utils.VWOLog;
import com.vwo.sampleapp.activities.MainActivity;
import com.vwo.sampleapp.utils.Constants;
import com.vwo.sampleapp.utils.SharedPreferencesHelper;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class LoginActivity extends AppCompatActivity {

    private Button bt_login, bt_getVariation, bt_trackGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Stetho.initializeWithDefaults(this);
        bt_login = findViewById(R.id.bt_login);
        bt_getVariation = findViewById(R.id.bt_getVariation);
        bt_trackGoal = findViewById(R.id.bt_track);

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initVWO("653a9dcd6c43ce70ec730c9af3c30594-469557", false, null);
            }
        });

        bt_getVariation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String variationName =   VWO.getVariationNameForTestKey("variable");
            }
        });

        bt_trackGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VWO.trackConversion(Constants.VWOKeys.GOAL_UPGRADE_CLICKED);
            }
        });
    }

    private void initVWO(
            String key, final boolean showProgress, @Nullable Map<String, String> keys) {
        Log.d("INIT", "Calling initVWO");

        VWOLog.setLogLevel(VWOLog.ALL);
        VWOConfig.Builder vwoConfigBuilder = new VWOConfig.Builder();
        //            vwoConfigBuilder.disablePreview();
        vwoConfigBuilder.setOptOut(false);
        vwoConfigBuilder.userID("sanyam jain");
        if (keys == null) {
            keys = new HashMap<>();
        }
        //            keys.put("userType", "free");
        vwoConfigBuilder.setCustomVariables(keys);


//        VWO.with(this, key).config((vwoConfigBuilder.build())).launchSynchronously(3000);


        VWO.with(this, key).config(vwoConfigBuilder.build()).launch(new VWOStatusListener() {
            @Override
            public void onVWOLoaded() {
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
            @Override
            public void onVWOLoadFailure(String s) {
                Log.d("LoginActivity", "onVWOLoadFailure: SDK Initialization failed");
            }
        });
        VWO.setCustomVariable("userType", "free");
    }

}
