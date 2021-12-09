package com.vwo.sampleapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.vwo.mobile.VWO;
import com.vwo.mobile.VWOConfig;
import com.vwo.mobile.events.VWOStatusListener;
import com.vwo.mobile.utils.VWOLog;
import com.vwo.sampleapp.R;
import com.vwo.sampleapp.fragments.FragmentHousingMain;
import com.vwo.sampleapp.fragments.FragmentSortingMain;
import com.vwo.sampleapp.interfaces.NavigationToggleListener;
import com.vwo.sampleapp.utils.Constants;
import com.vwo.sampleapp.utils.SharedPreferencesHelper;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, NavigationToggleListener {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String TAG_HOUSING = "housing";
    private static final String TAG_SORTING = "sorting";

    private static final int ID_FRAGMENT_SORTING = 0;
    private static final int ID_FRAGMENT_HOUSING = 1;
    private ProgressBar progressBar;
    private NavigationView navigationView;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            String campaignId = extras.getString(VWO.Constants.ARG_CAMPAIGN_ID);
            String campaignName = extras.getString(VWO.Constants.ARG_CAMPAIGN_NAME);
            String variationId = extras.getString(VWO.Constants.ARG_VARIATION_ID);
            String variationName = extras.getString(VWO.Constants.ARG_VARIATION_NAME);
            // Write your Analytics code here
            Log.d("BroadcastReceiver", String.format("User became part of Campaign %s with id %s " +
                    "\nVariation %s with id %s", campaignName, campaignId, variationName, variationId));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_layout_campaign);

        progressBar = findViewById(R.id.loading_progress);
        Intent intent = getIntent();

        Uri data = intent.getData();
        if (data != null) {
            if (data.getPathSegments().size() == 2) {
                String apiKey = data.getPathSegments().get(1);
                if (!TextUtils.isEmpty(apiKey)) {

                    Log.d(LOG_TAG, "API_KEY: " + apiKey);

                    Set<String> list = data.getQueryParameterNames();
                    Map<String, String> customKeys = new HashMap<>();
                    for (String key : list) {
                        if (key.startsWith("__vwo__")) {
                            customKeys.put(key.replace("__vwo__", ""), data.getQueryParameter(key));
                            Log.d(LOG_TAG, String.format(Locale.ENGLISH, "KEY: %s, VALUE: %s", key.replace("__vwo__", ""), data.getQueryParameter(key)));
                        }
                    }

                    // Do something with idString
                    if (validateAndSetApiKey(apiKey)) {
                        initVWO(apiKey, false, customKeys);
                    }
                } else {
                    initVWO(SharedPreferencesHelper.getApiKey(this), false, null);
                }
            }
        } else {
            initVWO(SharedPreferencesHelper.getApiKey(this), false, null);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(VWO.Constants.NOTIFY_USER_TRACKING_STARTED);

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_layout_campaign) {
            loadFragment(null, ID_FRAGMENT_SORTING, TAG_SORTING);
        } else if (id == R.id.nav_onboarding_campaign) {
            loadFragment(null, ID_FRAGMENT_HOUSING, TAG_HOUSING);
        } else if (id == R.id.action_clear_data) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.popup_theme);
            builder.setTitle(getString(R.string.confirm));
            builder.setMessage(getString(R.string.clear_data_message));
            builder.setNegativeButton(R.string.clear_data_negative, (dialogInterface, i) -> dialogInterface.dismiss());
            builder.setPositiveButton(R.string.clear_data_positive, (dialogInterface, i) -> {
                SharedPreferencesHelper.clearData(MainActivity.this);
                dialogInterface.dismiss();
                Toast.makeText(MainActivity.this, getString(R.string.data_cleared), Toast.LENGTH_SHORT).show();
            });
            builder.show();
        } else if (id == R.id.action_enter_api_key) {
            showApiKeyDialog();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showApiKeyDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.popup_theme);
        builder.setTitle(getString(R.string.title_api_key));

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.input_dialog, null, false);
        // Set up the input
        final TextInputLayout textInputLayout = viewInflated.findViewById(R.id.input_wrapper);
        final AppCompatEditText input = textInputLayout.findViewById(R.id.input);
        final AppCompatTextView currentKeyTextView = viewInflated.findViewById(R.id.current_key);
        if (SharedPreferencesHelper.getApiKey(this) != null) {
            currentKeyTextView.setText(getString(R.string.str_current_api_key, SharedPreferencesHelper.getApiKey(this)));
        } else {
            currentKeyTextView.setVisibility(View.GONE);
        }
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayout.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Set up the buttons
        builder.setPositiveButton("Launch VWO", null);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialog -> {
            Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                String apiKey = input.getText().toString().trim();

                if (validateAndSetApiKey(apiKey)) {
                    initVWO(apiKey, false, null);
                    alertDialog.dismiss();
                } else {
                    textInputLayout.setErrorEnabled(true);
                    textInputLayout.setError(getString(R.string.error_api_key));
                }
            });
        });

        alertDialog.show();
    }

    @CheckResult
    private boolean validateAndSetApiKey(String apiKey) {
        String regex = "[\\w]{32}-[0-9]*";
        Pattern pattern = Pattern.compile(regex);

        if (!TextUtils.isEmpty(apiKey) && pattern.matcher(apiKey).matches()) {
            Toast.makeText(MainActivity.this, getString(R.string.api_key_set), Toast.LENGTH_SHORT).show();
            SharedPreferencesHelper.setApiKey(apiKey, MainActivity.this);
            return true;
        }

        return false;
    }


    private void initVWO(String key, final boolean showProgress, @Nullable Map<String, String> keys) {
        Log.d("INIT", "Calling initVWO");
        if (!TextUtils.isEmpty(key)) {
            if (showProgress) {
                progressBar.setVisibility(View.VISIBLE);
            }
            VWOLog.setLogLevel(VWOLog.ALL);
            VWOConfig.Builder vwoConfigBuilder = new VWOConfig.Builder();
//            vwoConfigBuilder.disablePreview();
            vwoConfigBuilder.setOptOut(false);
//            vwoConfigBuilder.isChinaCDN(false);
            vwoConfigBuilder.userID("userId");
            if (keys == null) {
                keys = new HashMap<>();
            }
//            keys.put("userType", "free");
            vwoConfigBuilder.setCustomVariables(keys);
//            if (!TextUtils.isEmpty(Constants.VWOKeys.CUSTOM_DIMENSION_KEY) && !TextUtils.isEmpty(Constants.VWOKeys.CUSTOM_DIMENSION_VALUE)) {
//                vwoConfigBuilder.setCustomDimension(Constants.VWOKeys.CUSTOM_DIMENSION_KEY, Constants.VWOKeys.CUSTOM_DIMENSION_VALUE);
//            }

            VWO.with(this, key).config(vwoConfigBuilder.build()).launch(new VWOStatusListener() {
                @Override
                public void onVWOLoaded() {
                    if (showProgress) {
                        progressBar.setVisibility(View.GONE);
                    }
                    loadFragments();
                }

                @Override
                public void onVWOLoadFailure(String s) {
                    if (showProgress) {
                        progressBar.setVisibility(View.GONE);
                    }
                    loadFragments();
                }
            });
            VWO.setCustomVariable("userType", "free");
        } else {
            progressBar.setVisibility(View.GONE);
            loadFragments();
        }
    }

    private void loadFragments() {
        int fragmentID = getCurrentFragmentID();
        if (fragmentID == ID_FRAGMENT_HOUSING) {
            new Handler(getMainLooper()).post(() -> loadFragment(null, ID_FRAGMENT_HOUSING, TAG_HOUSING));
        } else {
            new Handler(getMainLooper()).post(() -> loadFragment(null, ID_FRAGMENT_SORTING, TAG_SORTING));
            // Uncomment below to Auto Reload
/*            FragmentSortingMain fragmentSortingMain = (FragmentSortingMain) getSupportFragmentManager().findFragmentByTag(TAG_SORTING);
            if(fragmentSortingMain != null && fragmentSortingMain.isVisible()) {
                fragmentSortingMain.refreshChildFragments();
            } else {
                new Handler(getMainLooper()).post(() -> loadFragment(null, ID_FRAGMENT_SORTING, TAG_SORTING));
            }*/
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void loadFragment(@Nullable Bundle bundle, int fragmentId, @Nullable String tag) {
        Log.d("FragmentID", "Loading fragment with id : " + fragmentId);
        switch (fragmentId) {
            case ID_FRAGMENT_SORTING:
                if (getCurrentFragmentID() != fragmentId) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container_main, new FragmentSortingMain(), tag).commit();
                }
                navigationView.setCheckedItem(R.id.nav_layout_campaign);
                break;
            case ID_FRAGMENT_HOUSING:
                if (getCurrentFragmentID() != fragmentId) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container_main, new FragmentHousingMain(), tag).commit();
                }
                navigationView.setCheckedItem(R.id.nav_onboarding_campaign);
                break;
        }
        super.loadFragment(bundle, fragmentId, tag);
    }

    @Override
    public void onToggle() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }
}
